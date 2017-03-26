/*
 * Copyright (C) 2014 cyann
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.cyann.tools;

import com.dmurph.tracking.AnalyticsConfigData;
import com.dmurph.tracking.JGoogleAnalyticsTracker;
import com.dmurph.tracking.JGoogleAnalyticsTracker.GoogleAnalyticsVersion;
import fr.cyann.algoide.AlgoIDEConstant;
import java.util.Locale;

/**
 * The GATracker class. Creation date: 8 juin 2013.
 * <p>
 * @author CyaNn
 * @version v0.1
 */
public class GATracker {

	private static GATracker singleton;
	private static boolean isAvoided = false;
	private JGoogleAnalyticsTracker tracker = null;

	private GATracker() {
		if (!isAvoided) {
			AnalyticsConfigData config = new AnalyticsConfigData(AlgoIDEConstant.ANALYTICS_UID);
			config.setUserLanguage(Locale.getDefault().toString());
			config.populateFromSystem();
			tracker = new JGoogleAnalyticsTracker(config, GoogleAnalyticsVersion.V_4_7_2);
			tracker.setDispatchMode(JGoogleAnalyticsTracker.DispatchMode.SINGLE_THREAD);
		}
	}

	public static GATracker getInstance() {
		if (singleton == null) {
			singleton = new GATracker();
		}
		return singleton;
	}

	public static void avoid() {
		isAvoided = true;
	}

	public void trackPageView(String argPageURL, String argPageTitle, String argHostName) {
		if (tracker != null) {
			tracker.trackPageView(argPageURL, argPageTitle, argHostName);
		}
	}

	public void trackEvent(String argCategory, String argAction) {
		if (tracker != null) {
			tracker.trackEvent(argCategory, argAction);
		}
	}

	public void trackEvent(String argCategory, String argAction, String argLabel) {
		if (tracker != null) {
			tracker.trackEvent(argCategory, argAction, argLabel);
		}
	}

	public void trackEvent(String argCategory, String argAction, String argLabel, Integer argValue) {
		if (tracker != null) {
			tracker.trackEvent(argCategory, argAction, argLabel, argValue);
		}
	}
}
