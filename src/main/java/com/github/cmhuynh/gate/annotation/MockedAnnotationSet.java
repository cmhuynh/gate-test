package com.github.cmhuynh.gate.annotation;

import gate.*;
import gate.annotation.ImmutableAnnotationSetImpl;
import gate.corpora.DocumentImpl;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;

/**
 * The mocked {@link AnnotationSet} which is missing as there is no test .jar for GATE
 *
 * @author Chau Huynh cmhuynh at gmail.com
 */
public class MockedAnnotationSet
        extends ImmutableAnnotationSetImpl
        implements AnnotationSet {

    /**
     * A mocked {@link DocumentImpl} from which GATE requires to initiate a new {@link AnnotationSet}
     */
    protected DocumentImpl document;

    /**
     * Collection of {@link Annotation} which this mocked {@link AnnotationSet} contains
     */
    protected Collection<Annotation> annotations = new ArrayList<>();

    /**
     * Use {@link Builder} to construct your {@link AnnotationSet} instead
     */
    private MockedAnnotationSet(DocumentImpl document, Collection<Annotation> annotations) {
        super(document, annotations);
        this.document = document;
        this.annotations.addAll(annotations);
    }

    /**
     * Get the {@link Builder} to build the mocked {@link AnnotationSet}
     *
     * @return the builder
     */
    public static Builder builder() {
        return new Builder();
    }

    private Predicate<Annotation> typeAndFeatureFilter(String type, FeatureMap constraints) {
        return annotation -> annotation.getType().equals(type)
                && annotation.getFeatures().subsumes(constraints);
    }

    private AnnotationSet asAnnotationSet(Collection<Annotation> annotations) {
        if(!annotations.isEmpty()) {
            return new ImmutableAnnotationSetImpl(document, annotations);
        } else {
            return emptyAnnotationSet;
        }
    }

    @Override
    public AnnotationSet get(String type, FeatureMap constraints) {
        Predicate<Annotation> filter = typeAndFeatureFilter(type, constraints);
        Collection<Annotation> result = annotations.stream()
                .filter(filter)
                .collect(Collectors.toList());
        return asAnnotationSet(result);
    }

    @Override
    public AnnotationSet get(String type, Set<? extends Object> featureNames) {
        Predicate<Annotation> filter = annotation -> annotation.getType().equals(type)
                && annotation.getFeatures().keySet().containsAll(featureNames);
        Collection<Annotation> result = annotations.stream()
                .filter(filter)
                .collect(Collectors.toList());
        return builder()
                .withDocument(document)
                .addAnnotations(result)
                .mock();
    }

    @Override
    public AnnotationSet get(String type, FeatureMap constraints, Long offset) {
        Predicate<Annotation> filter = typeAndFeatureFilter(type, constraints);
        Collection<Annotation> result = getAnnotations(offset).stream()
                .filter(filter)
                .collect(Collectors.toList());
        return asAnnotationSet(result);
    }

    private Collection<Annotation> getAnnotations(Long offset) {
        Optional<Annotation> startNodeOpt = annotations.stream()
                .filter(annotation -> annotation.getStartNode().getOffset() >= offset)
                .collect(Collectors.minBy(offsetComparator()));
        if (startNodeOpt.isPresent()) {
            long startOffset = startNodeOpt.map(annotation -> annotation.getStartNode().getOffset()).orElse(null);
            Predicate<Annotation> filter = annotation -> annotation.getStartNode().getOffset() == startOffset;
            return annotations.stream()
                    .filter(filter)
                    .collect(Collectors.toList());
        } else {
            return emptyList();
        }
    }

    private Comparator<Annotation> offsetComparator() {
        return (o1, o2) -> o1.getStartNode().getOffset().compareTo(o2.getStartNode().getOffset());
    }

    @Override
    public AnnotationSet get(Long offset) {
        Collection<Annotation> result = getAnnotations(offset);
        return asAnnotationSet(result);
    }

    @Override
    public AnnotationSet get(Long startOffset, Long endOffset) {
        Predicate<Annotation> filter = filterByOffsets(startOffset, endOffset);
        Collection<Annotation> result = annotations.stream()
                .filter(filter)
                .collect(Collectors.toList());
        return asAnnotationSet(result);
    }

    private Predicate<Annotation> filterByOffsets(Long startOffset, Long endOffset) {
        return annotation -> (annotation.getStartNode().getOffset() < endOffset
                && annotation.getEndNode().getOffset() > startOffset);
    }

    @Override
    public AnnotationSet get(String type, Long startOffset, Long endOffset) {
        Predicate<Annotation> filter = annotation -> annotation.getType().equals(type)
                && filterByOffsets(startOffset, endOffset).test(annotation);
        Collection<Annotation> result = annotations.stream()
                .filter(filter)
                .collect(Collectors.toList());
        return asAnnotationSet(result);
    }

    @Override
    public AnnotationSet getCovering(String neededType, Long startOffset, Long endOffset) {
        Predicate<Annotation> filter = annotation -> annotation.getType().equals(neededType)
                && annotation.getStartNode().getOffset() <= startOffset
                && annotation.getEndNode().getOffset() >= endOffset;
        Collection<Annotation> result = annotations.stream()
                .filter(filter)
                .collect(Collectors.toList());
        return asAnnotationSet(result);
    }

    @Override
    public AnnotationSet getContained(Long startOffset, Long endOffset) {
        Predicate<Annotation> filter = annotation ->
                annotation.getStartNode().getOffset() >= startOffset
                        && annotation.getEndNode().getOffset() <= endOffset;
        List<Annotation> result = annotations.stream()
                .filter(filter)
                .collect(Collectors.toList());
        return asAnnotationSet(result);
    }

    @Override
    public List<Annotation> inDocumentOrder() {
        List<Annotation> result = new ArrayList<>(annotations);
        Collections.sort(result, offsetComparator());
        return result;
    }

    @Override
    public Node firstNode() {
        return annotations.stream()
                .collect(Collectors.minBy(offsetComparator()))
                .map(Annotation::getStartNode)
                .orElse(null);
    }

    @Override
    public Node lastNode() {
        return annotations.stream()
                .collect(Collectors.maxBy(offsetComparator()))
                .map(Annotation::getStartNode)
                .orElse(null);
    }

    @Override
    public Node nextNode(Node node) {
        return inDocumentOrder().stream()
                .filter(annotation -> annotation.getStartNode().getOffset() > node.getOffset())
                .findFirst()
                .map(Annotation::getStartNode)
                .orElse(null);
    }

    @Override
    public Iterator<Annotation> iterator() {
        return annotations.iterator();
    }

    @Override
    public int size() {
        return annotations.size();
    }

    @Override
    public Annotation get(Integer id) {
        Predicate<Annotation> filter = annotation -> annotation.getId().equals(id);
        return annotations.stream()
                .filter(filter)
                .findFirst()
                .orElse(null);
    }

    @Override
    public AnnotationSet get() {
        return this;
    }

    @Override
    public AnnotationSet get(String type) {
        return get(singleton(type));
    }

    @Override
    public AnnotationSet get(Set<String> types) {
        Predicate<Annotation> filter = annotation -> types.contains(annotation.getType());
        Collection<Annotation> result = annotations.stream()
                .filter(filter)
                .collect(Collectors.toList());
        return asAnnotationSet(result);
    }

    @Override
    public Set<String> getAllTypes() {
        return annotations.stream()
                .map(Annotation::getType)
                .collect(Collectors.toSet());
    }

    /**
     * Builder to help construct your {@link AnnotationSet}
     */
    public static class Builder {
        /**
         * A mocked {@link DocumentImpl} from which GATE requires to initiate a new {@link AnnotationSet}
         */
        protected DocumentImpl document;
        /**
         * Collection of {@link Annotation} which this mocked {@link AnnotationSet} contains
         */
        private List<Annotation> annotations = new ArrayList<>();
        /**
         * Internal map of start offset and {@link Node}
         */
        private Map<Long, Node> startNodesByOffset = new HashMap<>();
        /**
         * Internal map of end offset and {@link Node}
         */
        private Map<Long, Node> endNodesByOffset = new HashMap<>();

        private Builder() {

        }

        /**
         * Supply the builder with a mock {@link DocumentImpl}, typically an object from a mocking framework of your choice.
         * The rationale is GATE implementation requires that {@link AnnotationSet} can only be constructed by annotating {@link Document}
         * or from an existing {@link AnnotationSet}
         * <p>
         * So you think you can use interface {@link Document}, but GATE throws {@link ClassCastException}
         */
        public Builder withDocument(DocumentImpl document) {
            this.document = document;
            return this;
        }

        /**
         * Add a {@link Annotation} into this {@link AnnotationSet}
         *
         * @param annotation the contained annotation
         * @return the builder
         */
        public Builder addAnnotation(Annotation annotation) {
            assert annotation.getStartNode() != null && annotation.getStartNode().getOffset() != null : "Start offset is required";
            assert annotation.getEndNode() != null && annotation.getEndNode().getOffset() != null : "End offset is required";

            Node start = annotation.getStartNode();
            Node end = annotation.getEndNode();
            startNodesByOffset.putIfAbsent(start.getOffset(), start);
            endNodesByOffset.putIfAbsent(end.getOffset(), end);

            Annotation gateHappyAnnotation = new MockedAnnotation(annotation.getId(),
                    startNodesByOffset.get(start.getOffset()),
                    endNodesByOffset.get(end.getOffset()),
                    annotation.getType(),
                    annotation.getFeatures());
            annotations.add(gateHappyAnnotation);

            return this;
        }

        /**
         * Add a collection of {@link Annotation} into this {@link AnnotationSet}
         *
         * @param annotations the contained annotations
         * @return the builder
         */
        public Builder addAnnotations(Collection<Annotation> annotations) {
            annotations.forEach(this::addAnnotation);
            return this;
        }

        /**
         * Construct a GATE-compliance {@link AnnotationSet} from your collections of {@link Annotation}
         *
         * @return the {@link AnnotationSet}
         */
        public AnnotationSet mock() {
            assert document != null : "Need a mocked Document object please";

            return new MockedAnnotationSet(document, annotations);
        }
    }
}
