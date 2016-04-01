/**
 * Copyright 2006-2011 Tuukka Haapasalo
 * 
 * This file is part of jAlkaMetri.
 * 
 * jAlkaMetri is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * 
 * jAlkaMetri is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with jAlkaMetri (LICENSE.txt). If not, see <http://www.gnu.org/licenses/>.
 */
package fi.tuska.jalkametri.util;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import fi.tuska.jalkametri.PrivateData;
import fi.tuska.jalkametri.R;
import fi.tuska.jalkametri.dao.Preferences;

public final class AdUtil {

    private static AdView adView;

    public AdUtil() {
    }

    /**
     * Check whether ads must be shown to this user.
     * 
     * @return true if ads must be shown
     */
    public boolean isAdsShown(Preferences prefs) {
        // Ads are enabled if either (1) the license is not bought (forced
        // ads); or (2) the user has selected to show ads.
        return !prefs.isLicensePurchased() || prefs.isAdsEnabled();
    }

    /**
     * Sets the ads visible/invisible based on license purchasing status.
     * Usable only when the license has just been bought; when the license is
     * bought and the app has been restarted, the ad view is not even created.
     */
    public void updateVisibility(Preferences prefs) {
        synchronized (AdUtil.class) {
            if (adView != null) {
                boolean adsVisible = isAdsShown(prefs);
                adView.setVisibility(adsVisible ? View.VISIBLE : View.GONE);
            }
        }
    }

    /**
     * Add the ads to the given activity. Looks for a layout names
     * "main_view".
     */
    public boolean showAds(Activity activity, Preferences prefs) {
        if (!isAdsShown(prefs)) {
            // If ads are not to be shown, do nothign
            return true;
        }
        // Lookup the layout
        final LinearLayout layout = (LinearLayout) activity.findViewById(R.id.main_view);
        if (layout == null)
            return false;

        // Create the adView
        synchronized (AdUtil.class) {
            if (adView == null) {
                // The PrivateData interface where the publisher identifier
                // (ADMOB_PUBLISHER_ID) resides is not shared to Subversion
                // for security. Use your own identifiers!
                adView = new AdView(activity, AdSize.BANNER, PrivateData.ADMOB_PUBLISHER_ID);
                assert adView != null;

                adView.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
                LayoutParams params = adView.getLayoutParams();
                params.width = LayoutParams.FILL_PARENT;
                adView.setLayoutParams(params);

                // Add the adView to the layout
                layout.addView(adView);
            }

            // Initiate a generic request to load it with an ad
            adView.loadAd(new AdRequest());
        }
        return true;
    }

    /**
     * Call to stop displaying the ad
     */
    public void destroyAd() {
        synchronized (AdUtil.class) {
            if (adView != null) {
                adView.stopLoading();
                adView = null;
            }
        }
    }

}
