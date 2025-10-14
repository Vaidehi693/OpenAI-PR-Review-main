/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.OpenAIPrReview.fulfilmentprocess.jalo;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import com.hybris.OpenAIPrReview.fulfilmentprocess.constants.OpenAIPrReviewFulfilmentProcessConstants;

public class OpenAIPrReviewFulfilmentProcessManager extends GeneratedOpenAIPrReviewFulfilmentProcessManager
{
	public static final OpenAIPrReviewFulfilmentProcessManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (OpenAIPrReviewFulfilmentProcessManager) em.getExtension(OpenAIPrReviewFulfilmentProcessConstants.EXTENSIONNAME);
	}
	
}
