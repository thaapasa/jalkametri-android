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
package fi.tuska.jalkametri;

/**
 * Private data. Change the name of this class to PrivateData, and configure
 * the constants to your liking.
 * 
 * @author Tuukka Haapasalo
 */
public interface PrivateDataTemplate {

    /**
     * Enables developer functionality.
     */
    final boolean DEVELOPER_FUNCTIONALITY_ENABLED = false;

    /**
     * Android Market publishing guide at
     * http://developer.android.com/guide/publishing/preparing.html says that
     * logging should be "deactivated" for applications that are published to
     * the App Market.
     * 
     * jAlkaMetri checks whether to do logging by checking this variable.
     */
    boolean LOGGING_ENABLED = false;

    /**
     * Set to true to enable ACRA error reporting (which sends stack traces to
     * Google Docs).
     */
    final boolean ACRA_ERROR_REPORTING_ENABLED = false;

    /** AdMob publisher ID */
    String ADMOB_PUBLISHER_ID = "my-admob-publisher-id";

    /** The real product ID */
    String JALCOMETER_REAL_LICENSE_PRODUCT_ID = "product_id_in_android_market";
    /** Test product IDs */
    String TEST_PRODUCT_ID_PURCHASED = "android.test.purchased";
    String TEST_PRODUCT_ID_CANCELED = "android.test.canceled";
    String TEST_PRODUCT_ID_REFUNDED = "android.test.refunded";
    String TEST_PRODUCT_ID_ITEM_UNAVAILABLE = "android.test.item_unavailable";
    /** Product ID for the jAlcoMeter license */
    String LICENSE_PRODUCT_ID = JALCOMETER_REAL_LICENSE_PRODUCT_ID;

    /** License purchasing payload */
    String LICENSE_PURCHASE_PAYLOAD = "license_purchasing_payload";

    /** Form key of the Google Docs form where stack traces are sent */
    String STACK_TRACE_DOCS_FORM_KEY = "form_key_for_google_docs_document";

    /** The Market account public key for verification */
    String ACCOUNT_PUBLIC_KEY = "android_market_public_key";

}
