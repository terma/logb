package com.github.terma.logb.server;

import com.github.terma.logb.criteria.AndCriteriaRequest;
import com.github.terma.logb.ListRequest;
import com.github.terma.logb.criteria.OrCriteriaRequest;
import com.github.terma.logb.criteria.PlainCriteriaRequest;
import com.github.terma.logb.criteria.RegexCriteriaRequest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

public class ListRequestParserTest {

    @Test
    public void nullAsNoLimits() {
        ListRequest listRequest = ListRequestParser.parse(null);
        Assert.assertThat(listRequest, CoreMatchers.equalTo(new ListRequest()));
    }

    @Test
    public void emptyAsNoLimits() {
        ListRequest listRequest = ListRequestParser.parse(null);
        Assert.assertThat(listRequest, CoreMatchers.equalTo(new ListRequest()));
    }

    @Test
    public void plainFileLimit() {
        ListRequest listRequest = ListRequestParser.parse("{file: {type: 'plain', value: 'xxx'}}");
        Assert.assertThat(listRequest, CoreMatchers.equalTo(new ListRequest(new PlainCriteriaRequest("xxx"))));
    }

    @Test
    public void regexFileLimit() {
        ListRequest listRequest = ListRequestParser.parse("{file: {type: 'regex', value: 'xxx'}}");
        Assert.assertThat(listRequest, CoreMatchers.equalTo(new ListRequest(new RegexCriteriaRequest("xxx"))));
    }

    @Test
    public void andLimit() {
        ListRequest listRequest = ListRequestParser.parse("{file: {type: 'and', left: {type: 'regex', value: 'rg'}, right: {type: 'plain', value: 'xxx'}}}");
        Assert.assertThat(listRequest, CoreMatchers.equalTo(new ListRequest(new AndCriteriaRequest(
                new RegexCriteriaRequest("rg"), new PlainCriteriaRequest("xxx")))));
    }

    @Test
    public void or() {
        ListRequest listRequest = ListRequestParser.parse("{file: {type: 'or', left: {type: 'regex', value: 'rg'}, right: {type: 'plain', value: 'xxx'}}}");
        Assert.assertThat(listRequest, CoreMatchers.equalTo(new ListRequest(new OrCriteriaRequest(
                new RegexCriteriaRequest("rg"), new PlainCriteriaRequest("xxx")))));
    }

}
