package com.github.terma.logb.criteria;

import java.io.Serializable;

public interface CriteriaRequest extends Serializable {

    boolean accept(String string);

}
