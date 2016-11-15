package com.chuynh.gate.annotation;

import gate.*;
import gate.corpora.DocumentImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.core.Is.is;

/**
 * @author Chau Huynh cmhuynh at gmail.com
 */
@RunWith(MockitoJUnitRunner.class)
public class MockedAnnotationSetTest {
    @Mock
    private DocumentImpl document;

    private AnnotationSet annotationSet;
    private Annotation anno1, anno2, anno3;
    private String type1 = "type1";
    private String type2 = "another type1";
    private String featureKey = "feature1";
    private Set<Integer> featureValue = new HashSet<>(Arrays.asList(1, 2, 3));

    @Before
    public void setup() {
        anno1 = MockedAnnotation.builder()
                .withType(type1)
                .withOffset(20, 30)
                .mock();
        anno2 = MockedAnnotation.builder()
                .withType(type2)
                .withOffset(40, 50)
                .mock();
        anno3 = MockedAnnotation.builder()
                .withType(type1)
                .withOffset(60, 70)
                .withFeature(featureKey, featureValue)
                .mock();
        annotationSet = MockedAnnotationSet.builder()
                .withDocument(document)
                .addAnnotations(Arrays.asList(anno1, anno2, anno3))
                .mock();
    }

    private List<Annotation> asList(AnnotationSet annotationSet) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(annotationSet.iterator(), Spliterator.ORDERED),
                false)
                .collect(Collectors.toList());
    }

    @Test
    public void test_get_by_type_and_feature() {
        FeatureMap constraints = Factory.newFeatureMap();
        constraints.put(featureKey, featureValue);

        List<Annotation> actual = asList(annotationSet.get(type1, constraints));
        assertThat(actual, is(singletonList(anno3)));
    }

    @Test
    public void test_get_by_type_and_feature_keys() {
        Set<String> featureKeys = new HashSet<>();
        featureKeys.add(featureKey);
        List<Annotation> actual = asList(annotationSet.get(type1, featureKeys));

        assertThat(actual, is(singletonList(anno3)));
    }

    @Test
    public void test_get_by_type_and_feature_and_start_offset() {
        FeatureMap constraints = Factory.newFeatureMap();
        constraints.put(featureKey, featureValue);

        List<Annotation> actual = asList(annotationSet.get(type1, constraints, 50L));
        assertThat(actual, is(singletonList(anno3)));

        actual = asList(annotationSet.get(type1, constraints, 30L));
        assertThat(actual, empty());
    }

    @Test
    public void test_get_by_start_node() {
        List<Annotation> actual = asList(annotationSet.get(50L));
        assertThat(actual, is(singletonList(anno3)));
    }

    @Test
    public void test_get_by_strict_offset() {
        List<Annotation> actual = asList(annotationSet.get(5L, 60L));
        assertThat(actual, containsInAnyOrder(anno1, anno2));
    }

    @Test
    public void test_get_by_type_and_strict_offset() {
        List<Annotation> actual = asList(annotationSet.get(type1, 5L, 60L));
        assertThat(actual, is(singletonList(anno1)));
    }

    @Test
    public void test_get_covering() {
        List<Annotation> actual = asList(annotationSet.getCovering(type1, 25L, 30L));
        assertThat(actual, is(singletonList(anno1)));
    }

    @Test
    public void test_get_contained() {
        List<Annotation> actual = asList(annotationSet.getContained(20L, 60L));
        assertThat(actual, containsInAnyOrder(anno1, anno2));
    }

    @Test
    public void test_in_doc_order() {
        annotationSet = MockedAnnotationSet.builder()
                .withDocument(document)
                .addAnnotations(Arrays.asList(anno3, anno2, anno1))
                .mock();

        List<Annotation> actual = annotationSet.inDocumentOrder();
        assertThat(actual, is(Arrays.asList(anno1, anno2, anno3)));
    }

    @Test
    public void test_get_first_node() {
        Long offset = Optional.ofNullable(annotationSet.firstNode()).map(Node::getOffset).orElse(null);

        assertThat(offset, is(20L));
    }

    @Test
    public void test_get_last_node() {
        Long offset = Optional.ofNullable(annotationSet.lastNode()).map(Node::getOffset).orElse(null);

        assertThat(offset, is(60L));
    }

    @Test
    public void test_get_next_node() {
        Node first = annotationSet.firstNode();
        Long offset = Optional.ofNullable(annotationSet.nextNode(first)).map(Node::getOffset).orElse(null);

        assertThat(offset, is(40L));
    }

    @Test
    public void test_iterator() {
        List<Annotation> result = new ArrayList<>();
        annotationSet.iterator().forEachRemaining(annotation -> result.add(annotation));

        assertThat(result, is(Arrays.asList(anno1, anno2, anno3)));
    }

    @Test
    public void test_size() {
        assertThat(annotationSet.size(), is(3));
    }

    @Test
    public void test_get_by_id() {
        Annotation actual = annotationSet.get(50L).iterator().next();
        Annotation result = annotationSet.get(actual.getId());

        assertThat(result, is(actual));

        assertThat(annotationSet.get(anno1.getId()), is(anno1));
    }

    @Test
    public void test_get_self() {
        assertThat(annotationSet.get(), is(annotationSet));
    }

    @Test
    public void test_get_by_type() {
        List<Annotation> result = asList(annotationSet.get(type1));

        assertThat(result, containsInAnyOrder(anno1, anno3));
    }

    @Test
    public void test_get_by_multiple_type() {
        List<Annotation> result = asList(annotationSet.get(new HashSet<>(Arrays.asList(type1, type2))));

        assertThat(result, containsInAnyOrder(anno1, anno2, anno3));
    }

    @Test
    public void test_get_all_types() {
        Set<String> result = annotationSet.getAllTypes();
        Set<String> expected = new HashSet<>(Arrays.asList(type1, type2));

        assertThat(result, is(expected));
    }
}