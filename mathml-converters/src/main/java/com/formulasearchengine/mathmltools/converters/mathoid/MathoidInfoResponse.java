package com.formulasearchengine.mathmltools.converters.mathoid;

/**
 * @author Andre Greiner-Petter
 */
public class MathoidInfoResponse {
    private boolean success;

    private String checked;

    private String[] requiredPackages;

    private String[] identifiers;

    private boolean endsWithDots;

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
