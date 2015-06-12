package com.github.terma.logb;

import com.github.terma.logb.criteria.CriteriaRequest;
import com.github.terma.logb.criteria.TrueCriteriaRequest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListRequest implements Serializable {

    public String app;
    public ArrayList<String> files;
    public CriteriaRequest file;
    public CriteriaRequest content = new TrueCriteriaRequest();

    public ListRequest() {
        this(new TrueCriteriaRequest());
    }

    public ListRequest(CriteriaRequest file) {
        this.file = file;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListRequest that = (ListRequest) o;
        return Objects.equals(app, that.app) &&
                Objects.equals(files, that.files) &&
                Objects.equals(file, that.file) &&
                Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(app, files, file, content);
    }

    @Override
    public String toString() {
        return "ListRequest{" + "app='" + app + '\'' + ", files=" + files + ", file=" + file + ", content=" + content + '}';
    }

}
