/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.OpenAIPrReview.core.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import com.hybris.OpenAIPrReview.core.constants.OpenAIPrReviewCoreConstants;
import com.hybris.OpenAIPrReview.core.setup.CoreSystemSetup;


/**
 * Do not use, please use {@link CoreSystemSetup} instead.
 * 
 */
public class OpenAIPrReviewCoreManager extends GeneratedOpenAIPrReviewCoreManager
{
	public static final OpenAIPrReviewCoreManager getInstance()
	{
		final ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (OpenAIPrReviewCoreManager) em.getExtension(OpenAIPrReviewCoreConstants.EXTENSIONNAME);
	}
}
