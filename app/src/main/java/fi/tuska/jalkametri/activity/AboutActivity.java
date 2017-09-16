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
package fi.tuska.jalkametri.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import fi.tuska.jalkametri.Common.ResponseCode;
import fi.tuska.jalkametri.R;
import fi.tuska.jalkametri.billing.BillingService;
import fi.tuska.jalkametri.billing.BillingService.RequestPurchase;
import fi.tuska.jalkametri.billing.BillingService.RestoreTransactions;
import fi.tuska.jalkametri.billing.PurchaseObserver;
import fi.tuska.jalkametri.billing.ResponseHandler;
import fi.tuska.jalkametri.util.DialogUtil;
import fi.tuska.jalkametri.util.LogUtil;

/**
 * Activity for showing an about screen. The screen shows information about the
 * software.
 *
 * @author Tuukka Haapasalo
 */
public class AboutActivity extends JalkametriActivity implements GUIActivity {

    private static final String TAG = "AboutActivity";

	private TextView versionNumber;
	private TextView versionText;
	private TextView versionShort;
	private Button checkLicenseButton;
	private Button purchaseButton;
	private TextView billingServiceError;

	private BillingService billingService;
	private Handler handler;
	private PurchaseObserver billingObserver;

	private boolean billingError = false;

	public AboutActivity() {
		super(R.string.title_about, R.string.help_about);
		setShowDefaultHelpMenu(true);
	}

	/*
	 * Standard activity functions --------------------------------------------
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);

		handler = new Handler(getMainLooper());
		billingObserver = new JalkametriPurchaseObserver(this, handler);
		versionNumber = (TextView) findViewById(R.id.version_number);
		versionText = (TextView) findViewById(R.id.version_info);
		versionShort = (TextView) findViewById(R.id.version_short);
		checkLicenseButton = (Button) findViewById(R.id.check_license);
		purchaseButton = (Button) findViewById(R.id.buy_license);
		billingServiceError = (TextView) findViewById(R.id.billing_service_error);

		initComponents();

		// Check if billing is supported.
		ResponseHandler.register(billingObserver);

		// Only fire up the license checking if the license has not been
		// purchased at some point.
		if (!prefs.isLicensePurchased()) {
			billingService = new BillingService();
			billingService.setContext(this);
			if (!billingService.checkBillingSupported()) {
				billingError = true;
			}
		}

		updateUI();
	}

	@Override
	protected void onStart() {
		super.onStart();
		ResponseHandler.register(billingObserver);
		updateUI();
	}

	@Override
	protected void onStop() {
		super.onStop();
		ResponseHandler.unregister(billingObserver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (billingService != null) {
			billingService.unbind();
			billingService = null;
		}
	}

	/*
	 * Activity item initialization --------------------------------------------
	 */
	private void initComponents() {
		setVersionNumber();
	}

	public void setVersionNumber() {
		try {
			String vNum = getPackageManager().getPackageInfo(getPackageName(),
					0).versionName;
			versionNumber.setText(vNum);
		} catch (NameNotFoundException e) {
			versionNumber.setText("0.0");
		}
	}

	@Override
	public void updateUI() {
		Resources res = getResources();
		final boolean hasLicense = prefs.isLicensePurchased();
		// Set the version text to reflect the license purchasing status
		versionShort.setText(res
				.getString(hasLicense ? R.string.version_licensed
						: R.string.version_free));
		versionText.setText(res
				.getString(hasLicense ? R.string.about_version_licensed
						: R.string.about_version_free));

		// Purchase & check license buttons are only shown when the user does
		// not have a license and no billing errors have occurred
		{
			int visibility = !hasLicense && !billingError ? View.VISIBLE
					: View.GONE;
			purchaseButton.setVisibility(visibility);
			checkLicenseButton.setVisibility(visibility);
		}

		// Billing error is only shown if the user does not have a license
		// (and an error has occurred)
		billingServiceError
				.setVisibility(!hasLicense && billingError ? View.VISIBLE
						: View.GONE);
	}

	/*
	 * Custom actions ----------------------------------------------------------
	 */
	public void onOKPressed(View v) {
		// Dismiss this activity when OK is pressed
		onBackPressed();
	}

	public void purchaseLicense(View v) {
		// Purchase the license
		if (prefs.isLicensePurchased()) {
			LogUtil.i(TAG, "License already bought, not repurchasing");
			// Already bought
			updateUI();
			return;
		}
		if (billingService != null) {
			LogUtil.i(TAG, "Starting to purchase license");
			// Request a license purchase
			if (billingService.purchaseLicense()) {
				Toast.makeText(AboutActivity.this,
						getResources().getString(R.string.msg_wait_for_market),
						Toast.LENGTH_LONG);
			} else {
				billingError = true;
				updateUI();
			}
		}
	}

	public void checkLicense(View v) {
		// Check for existing license
		if (prefs.isLicensePurchased()) {
			LogUtil.i(TAG, "License already bought, not checking");
			updateUI();
			return;
		}
		if (billingService != null) {
			LogUtil.i(TAG, "Starting to check license");
			// Request a license check
			if (billingService.restoreTransactions()) {
				Toast.makeText(
						this,
						getResources().getString(R.string.msg_checking_license),
						Toast.LENGTH_LONG).show();
			} else {
				billingError = true;
				updateUI();
			}
		}
	}

	private class JalkametriPurchaseObserver extends PurchaseObserver {

		public JalkametriPurchaseObserver(Activity activity, Handler handler) {
			super(activity, handler);
		}

		@Override
		public void onLicenseStateChanged(boolean hasLicense) {
			if (hasLicense) {
				DialogUtil.showMessage(AboutActivity.this,
						R.string.msg_purchase_successful,
						R.string.title_success);
			} else {
				DialogUtil.showMessage(AboutActivity.this,
						R.string.msg_purchase_failed, R.string.title_error);
			}
			updateUI();
		}

		@Override
		public void onBillingSupported(boolean supported) {
			if (!supported) {
				billingError = true;
				updateUI();
			}
		}

		@Override
		public void onRequestPurchaseResponse(RequestPurchase request,
				ResponseCode responseCode) {
			Resources res = getResources();
			if (responseCode == ResponseCode.RESULT_USER_CANCELED) {
				Toast.makeText(AboutActivity.this,
						res.getString(R.string.msg_purchase_canceled),
						Toast.LENGTH_LONG);
			}
		}

		@Override
		public void onRestoreTransactionsResponse(RestoreTransactions request,
				ResponseCode responseCode) {
			// Nada here
		}
	}

	/*
	 * Return from activities --------------------------------------------------
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		updateUI();
	}
}
