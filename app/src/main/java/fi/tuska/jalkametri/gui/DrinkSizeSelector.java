package fi.tuska.jalkametri.gui;

import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import fi.tuska.jalkametri.Common;
import fi.tuska.jalkametri.R;
import fi.tuska.jalkametri.dao.DrinkSizes;
import fi.tuska.jalkametri.data.DrinkSize;
import fi.tuska.jalkametri.data.IconName;
import fi.tuska.jalkametri.data.PreferencesImpl;
import fi.tuska.jalkametri.db.DBAdapter;
import fi.tuska.jalkametri.db.DrinkLibraryDB;
import fi.tuska.jalkametri.util.Converter;
import fi.tuska.jalkametri.util.LogUtil;
import fi.tuska.jalkametri.util.NumberUtil;
import fi.tuska.jalkametri.util.ObjectCallback;

public class DrinkSizeSelector {

    private static final String TAG = "DrinkSizeSelector";

    private Activity parent;

    private EditText sizeEdit;
    private EditText sizeNameEdit;
    private IconView sizeIcon;
    private DrinkSize spinnerSelection;
    private DrinkSize selectedDrinkSize;

    /**
     * Selection spinner for pre-existing size entries. This can be missing
     * from the edit form and therefore be null; this class is prepared to
     * handle that. In this case the pre-selection functionality is not
     * enabled.
     */
    private Spinner sizeSelectionSpinner;
    /**
     * Adapter for the spinner; this will be null if the spinner is null, see
     * the comments for the spinner.
     */
    private TextIconSpinnerAdapter<DrinkSize> sizeSelectionAdapter;

    /**
     * A CheckBox for checking whether a custom size can be entered. This
     * class is prepared for a null value for this control; i.e., it can be
     * missing from the edit form. In this case, custom editing is always
     * enabled.
     */
    private CheckBox modifySizeCheckbox;

    private final boolean selectorShown;
    private final boolean sizeIconEditorShown;
    private final int sizeSelectionDialogID;

    private final Locale locale;

    private DrinkSizes sizeLib;

    private OnClickListener modifyCheckBoxSelectListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (modifySizeCheckbox != null) {
                modifySizeCheckbox.setChecked(true);
            }
        }
    };

    private final ObjectCallback<IconName> iconNameCallback = new ObjectCallback<IconName>() {
        @Override
        public void objectSelected(IconName icon) {
            // Update icon
            LogUtil.d(TAG, "Selecting icon %s", icon.getIcon());
            sizeIcon.setIcon(icon);
        }
    };

    public DrinkSizeSelector(Activity parent, DBAdapter adapter, boolean selecterShown,
        boolean sizeIconEditorShown, int sizeSelectionDialogID) {
        this.parent = parent;
        this.sizeIconEditorShown = sizeIconEditorShown;
        this.selectorShown = selecterShown;
        this.sizeSelectionDialogID = sizeSelectionDialogID;
        this.sizeLib = new DrinkLibraryDB(adapter).getDrinkSizes();
        this.locale = new PreferencesImpl(parent).getLocale();
    }

    /**
     * @param initialSelection initially selected size; may be null (in this
     * case, selected the default size)
     */
    public void initializeComponents(DrinkSize initialSelection) {
        if (initialSelection == null) {
            initialSelection = sizeLib.getDefaultSize();
        }

        sizeEdit = (EditText) parent.findViewById(R.id.size_edit);
        sizeNameEdit = (EditText) parent.findViewById(R.id.size_name_edit);
        modifySizeCheckbox = (CheckBox) parent.findViewById(R.id.modify_size);
        sizeSelectionSpinner = (Spinner) parent.findViewById(R.id.size_selector);

        if (!selectorShown) {
            // Hide the entire size selector
            LogUtil.d(TAG, "Hiding size selector");
            View selectorArea = parent.findViewById(R.id.size_selection_area);
            assert selectorArea != null;
            selectorArea.setVisibility(View.GONE);
        } else {
            if (sizeIconEditorShown) {
                // Show the size icon editor
                View sizeArea = parent.findViewById(R.id.size_icon_area);
                sizeArea.setVisibility(View.VISIBLE);

                sizeIcon = (IconView) parent.findViewById(R.id.size_icon);
                sizeIcon.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Select an icon
                        if (isCustomSizeEditingEnabled()) {
                            // Select the icon from an icon selecting dialog
                            parent.showDialog(sizeSelectionDialogID);
                        }
                    }
                });
            }
        }
        // Modify size
        if (modifySizeCheckbox != null) {
            modifySizeCheckbox.setChecked(false);
        }
        updateSizeEditorEnabling();

        if (selectorShown && modifySizeCheckbox != null) {
            modifySizeCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    updateSizeEditorEnabling();
                }
            });
        }

        // Size selection spinner
        if (sizeSelectionSpinner != null) {
            populateSizeSelectionSpinner(initialSelection);
        }

        // Set size name/size edit clicking to toggle modification
        // checkbox
        sizeNameEdit.setOnClickListener(modifyCheckBoxSelectListener);
        sizeEdit.setOnClickListener(modifyCheckBoxSelectListener);

        setDrinkSize(initialSelection, true);

        if (selectorShown && sizeSelectionSpinner != null) {
            sizeSelectionSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapter, View view, int position,
                    long id) {
                    DrinkSize size = sizeSelectionAdapter.getItem(position);
                    if (spinnerSelection == null || !spinnerSelection.equals(size)) {
                        LogUtil.d(TAG, "DrinkSize item %s selected", size);
                        spinnerSelection = size;
                        setSizeSelected(size);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapter) {
                }
            });
        }
    }

    public ObjectCallback<IconName> getSetSizeIconCallback() {
        return iconNameCallback;
    }

    /**
     * Sets the given size.
     */
    public void setDrinkSize(DrinkSize size, boolean addIfMissing) {
        this.selectedDrinkSize = size;

        if (sizeSelectionAdapter == null) {
            // No size selection adapter, so just set the custom edit forms.
            populateCustomEditors(size);
            return;
        }

        // Find the selection from the sizeSelectionAdapter
        int pos = sizeSelectionAdapter.findItem(size);
        if (pos != -1) {
            // Size was found from the size list
            sizeSelectionSpinner.setSelection(pos);
            // Not a custom item, set the text to the text editors
            if (modifySizeCheckbox != null) {
                modifySizeCheckbox.setChecked(false);
            }
            spinnerSelection = size;
        } else {
            // Size was not found, so this is a custom size
            // Select first element from the spinner
            if (sizeSelectionAdapter.getItemCount() > 0) {
                sizeSelectionSpinner.setSelection(0);
                spinnerSelection = sizeSelectionAdapter.getItem(0);
            } else {
                spinnerSelection = null;
            }
            if (modifySizeCheckbox != null) {
                modifySizeCheckbox.setChecked(true);
            }
        }
        // Update the text editors
        sizeNameEdit.setText(size.getName());
        sizeEdit.setText(NumberUtil.toString(size.getVolume(), parent.getResources()));
        if (sizeIconEditorShown)
            sizeIcon.setIcon(size.getIcon());
    }

    private void populateCustomEditors(DrinkSize size) {
        sizeNameEdit.setText(size.getName());
        sizeEdit.setText(NumberUtil.toString(size.getVolume(), parent.getResources()));
        if (sizeIconEditorShown)
            sizeIcon.setIcon(size.getIcon());
    }

    /**
     * @return the currently selected drink size
     */
    public DrinkSize getDrinkSize() {
        if (isCustomSizeEditingEnabled()) {
            // Custom size selected
            String name = sizeNameEdit.getText().toString();
            double volume = NumberUtil.readDouble(sizeEdit.getText().toString(), locale);

            // Create a new size based on the entered data
            DrinkSize size = new DrinkSize(name, volume, getCurrentlySelectedIcon());
            return size;
        } else {
            // Return the selected drink
            return selectedDrinkSize;
        }
    }

    private boolean isCustomSizeEditingEnabled() {
        // If there is no modification button on the form, then it is assumed
        // that the size editing is enabled
        return modifySizeCheckbox == null || modifySizeCheckbox.isChecked();
    }

    private void setSizeSelected(DrinkSize size) {
        selectedDrinkSize = size;
        sizeEdit.setText(NumberUtil.toString(size.getVolume(), parent.getResources()));
        sizeNameEdit.setText(size.getName());
        if (modifySizeCheckbox != null) {
            modifySizeCheckbox.setChecked(false);
        }
        if (sizeIconEditorShown)
            sizeIcon.setIcon(size.getIcon());
        updateSizeEditorEnabling();
    }

    private void updateSizeEditorEnabling() {
        boolean controlsEnabled = isCustomSizeEditingEnabled();

        sizeNameEdit.setEnabled(controlsEnabled);
        sizeEdit.setEnabled(controlsEnabled);
        if (sizeIconEditorShown) {
            sizeIcon.setEnabled(controlsEnabled);
        }
    }

    /**
     * Must only be called when the spinner is present on the edit form.
     */
    private void populateSizeSelectionSpinner(DrinkSize initialSize) {
        List<DrinkSize> sizes = sizeLib.getAllSizes();

        sizeSelectionAdapter = new TextIconSpinnerAdapter<DrinkSize>(parent, sizes,
            new Converter<DrinkSize, String>() {
                @Override
                public String convert(DrinkSize item) {
                    return item.getIconText(parent.getResources());
                }
            }, new Converter<DrinkSize, String>() {
                @Override
                public String convert(DrinkSize item) {
                    return item.getIcon();
                }
            });
        sizeSelectionSpinner.setAdapter(sizeSelectionAdapter);
    }

    private String getCurrentlySelectedIcon() {
        if (sizeIconEditorShown)
            return sizeIcon.getIcon().getIcon();
        else
            return Common.DEFAULT_ICON_NAME;
    }

}
