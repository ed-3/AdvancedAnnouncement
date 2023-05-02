package me.ed333.toolkits.utils.version;

import org.jetbrains.annotations.NotNull;

public class VersionModifier {
    private final int version;
    private final String modifierName;

    public VersionModifier(String modifierName, int version) {
        this.modifierName = modifierName;
        this.version = version;
    }

    public String getModifierName() {
        return this.modifierName;
    }

    public int getVersion() {
        return this.version;
    }

    @NotNull
    public String contentToString() {
        return this.version == 0 ? this.modifierName : this.modifierName + this.version;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            VersionModifier that = (VersionModifier) o;
            return this.version == that.version && this.modifierName.equals(that.modifierName);
        } else {
            return false;
        }
    }
}
