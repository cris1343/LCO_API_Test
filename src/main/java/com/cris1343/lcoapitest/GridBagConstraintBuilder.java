package com.cris1343.lcoapitest;

import java.awt.*;

public class GridBagConstraintBuilder {
    private GridBagConstraints c;

    public GridBagConstraintBuilder() {
        c = new GridBagConstraints();
    }

    public GridBagConstraints build() {
        return c;
    }

    public GridBagConstraintBuilder pos(int x, int y) {
        c.gridx = x;
        c.gridy = y;
        return this;
    }

    public GridBagConstraintBuilder size(int width, int height) {
        c.gridwidth = width;
        c.gridheight = height;
        return this;
    }

    public GridBagConstraintBuilder fill(int fill) {
        c.fill = fill;
        return this;
    }

    //IPADX IPADY

    public GridBagConstraintBuilder insets(int top, int left, int bottom, int right) {
        c.insets = new Insets(top, left, bottom, right);
        return this;
    }

    public GridBagConstraintBuilder anchor(int anchor) {
        c.anchor = anchor;
        return this;
    }

    public GridBagConstraintBuilder weight(double weightX, double weightY) {
        c.weightx = weightX;
        c.weighty = weightY;
        return this;
    }
}
