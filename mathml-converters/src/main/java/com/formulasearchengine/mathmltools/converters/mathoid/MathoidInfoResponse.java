package com.formulasearchengine.mathmltools.converters.mathoid;

/**
 * @author Andre Greiner-Petter
 */
public class MathoidInfoResponse {
    private boolean success = false;

    private String checked = "";

    private String[] requiredPackages = new String[0];

    private String[] identifiers = new String[0];

    private boolean endsWithDots = false;

    public MathoidInfoResponse() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getChecked() {
        return checked;
    }

    public void setChecked(String checked) {
        this.checked = checked;
    }

    public String[] getRequiredPackages() {
        return requiredPackages;
    }

    public void setRequiredPackages(String[] requiredPackages) {
        this.requiredPackages = requiredPackages;
    }

    public String[] getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(String[] identifiers) {
        this.identifiers = identifiers;
    }

    public boolean isEndsWithDots() {
        return endsWithDots;
    }

    public void setEndsWithDots(boolean endsWithDots) {
        this.endsWithDots = endsWithDots;
    }
}
