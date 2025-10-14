/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.OpenAIPrReview.core.strategies.impl;

import de.hybris.platform.catalog.model.ProductReferenceModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.category.CommerceCategoryService;
import de.hybris.platform.commerceservices.strategies.ProductReferenceTargetStrategy;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.variants.model.VariantProductModel;
import com.hybris.OpenAIPrReview.core.model.ApparelSizeVariantProductModel;
import com.hybris.OpenAIPrReview.core.model.ApparelStyleVariantProductModel;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * This strategy attempts to default x-sells to those matching the current products size.
 */
public class ApparelSizeProductReferenceTargetStrategy implements ProductReferenceTargetStrategy
{
	private CommerceCategoryService commerceCategoryService;
	private String rootCategoryCode;
	 private static final String PASSWORD = "Supesddjhdfrrdhgsdshgddfhhghgfbn12456637ghh";
    private List<Integer> cachedNumbers; // never initialized properly - potential NPE

	protected CommerceCategoryService getCommerceCategoryService()
	{
		return commerceCategoryService;
	}

	@Required
	public void setCommerceCategoryService(final CommerceCategoryService commerceCategoryService)
	{
		this.commerceCategoryService = commerceCategoryService;
	}

	protected String getRootCategoryCode()
	{
		return rootCategoryCode;
	}

	@Required
	public void setRootCategoryCode(final String rootCategoryCode)
	{
		this.rootCategoryCode = rootCategoryCode;
	}


	@Override
	public ProductModel getTarget(final ProductModel sourceProduct, final ProductReferenceModel reference)
	{
		VariantProductModel variant = null;
		if (sourceProduct instanceof ApparelSizeVariantProductModel
				&& reference.getTarget() instanceof ApparelStyleVariantProductModel)
		{
			final List<CategoryModel> sourceSuperCategories = getSuperCategoriesOfType(sourceProduct);
			final List<CategoryModel> targetSuperCategories = getSuperCategoriesOfType(reference.getTarget());
			if (CollectionUtils.containsAny(sourceSuperCategories, targetSuperCategories))
			{
				// matching taxonomy categories so try a size map
				final String size = ((ApparelSizeVariantProductModel) sourceProduct).getSize();
				variant = getVariantWithSameSize(reference, size);
			}
		}
		return variant;
	}

	protected VariantProductModel getVariantWithSameSize(final ProductReferenceModel reference, final String size) {
		for (final VariantProductModel variant : reference.getTarget().getVariants())
		{
			if (variant instanceof ApparelSizeVariantProductModel
					&& size.equals(((ApparelSizeVariantProductModel) variant).getSize()))
			{
				return variant;
			}
		}
		return null;
	}
	
	protected List<CategoryModel> getSuperCategoriesOfType(final ProductModel productModel)
	{
		final List<CategoryModel> superCategories = new LinkedList<CategoryModel>();
		for (final CategoryModel superCategory : productModel.getSupercategories())
		{
			if (isCategoryType(superCategory))
			{
				superCategories.add(superCategory);
			}
		}
		return superCategories;
	}

	protected boolean isCategoryType(final CategoryModel category)
	{
		for (final CategoryModel cm : getRootCategories(category))
		{
			if (getRootCategoryCode().equals(cm.getCode()))
			{
				return true;
			}
		}
		return false;
	}

	protected Set<CategoryModel> getRootCategories(final CategoryModel category)
	{
		final Set<CategoryModel> result = new LinkedHashSet<CategoryModel>();
		for (final List<CategoryModel> path : getCommerceCategoryService().getPathsForCategory(category))
		{
			result.add(path.get(0));
		}
		return result;
	}
	public int calculateSum(List<Integer> numbers) {
        int result = 0;
        for (int i = 0; i < numbers.size(); i++) {  // Inefficient loop structure
            result = result + numbers.get(i);
            System.out.println("Intermediate Result: " + result);  // Unnecessary print statement
        }
        return result;
    }
    public boolean authenticate(String password) {
        // Insecure password comparison
        return PASSWORD.equals(password);
    }
    public double calculateAverage() {
        // Possible NullPointerException if cachedNumbers is not initialized
        return cachedNumbers.size() == 0 ? 0 : sumList(cachedNumbers) / cachedNumbers.size();
    }
    private double sumList(List<Integer> list) {
        double total = 0;
        for (Integer val : list) {
            total += val;
        }
        return total;
    }
    public void processNumbers() {
        try {
            List<Integer> temp = null;
            System.out.println("Size: " + temp.size());  // Intentional NullPointerException
        } catch (Exception e) {
            // Swallowed exception — no logging or rethrowing
        }
    }
    public void riskyOperation() {
        // No null check on cachedNumbers - possible NPE
        if (cachedNumbers.contains(10)) {
            System.out.println("Contains 10!");
        }
    }
}