package com.github.cmhuynh.gate.annotation;

import gate.Annotation;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * @author Chau Huynh cmhuynh at gmail.com
 */
public class MockedAnnotationTest {
    private String TYPE_VALUE = "some type";
    private String INST_VALUE = "some instance value";

    @Test
    public void test_mock() {
        String fKey = "some feature";
        Set<Integer> fValue = new HashSet<>(Arrays.asList(1, 2, 3));
        Annotation annotation = MockedAnnotation.builder().withInstance(INST_VALUE)
                .withType(TYPE_VALUE)
                .withOffset(10, 20)
                .withFeature(fKey, fValue)
                .mock();

        assertThat(annotation.getId(), notNullValue());
        assertThat(annotation.getStartNode().getOffset(), is(10L));
        assertThat(annotation.getEndNode().getOffset(), is(20L));
        assertThat(annotation.getType(), is(TYPE_VALUE));
        assertThat(annotation.getFeatures().get("inst"), is(INST_VALUE));
        assertThat(annotation.getFeatures().get(fKey), is(fValue));
    }

    @Test
    public void test_valueOf() {
        Annotation someAnno = MockedAnnotation.builder().withInstance(INST_VALUE)
                .withType(TYPE_VALUE)
                .withOffset(10, 20)
                .mock();
        Annotation annotation = MockedAnnotation.builder()
                .valueOf(someAnno)
                .mock();

        assertThat(annotation.getId(), notNullValue());
        assertThat(annotation.getStartNode().getOffset(), is(10L));
        assertThat(annotation.getEndNode().getOffset(), is(20L));
        assertThat(annotation.getType(), is(TYPE_VALUE));
        assertThat(annotation.getFeatures().get("inst"), is(INST_VALUE));
    }
}