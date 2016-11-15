package com.chuynh.gate.annotation;

import gate.Annotation;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Node;
import gate.annotation.AnnotationImpl;
import gate.annotation.NodeImpl;

import java.util.Optional;
import java.util.Random;

/**
 * A mocked implementation of {@link Annotation} which is missing because there is no test .jar for GATE
 *
 * Use {@link Builder} to construct your mocked {@link Annotation}
 *
 * @author Chau Huynh cmhuynh at gmail.com
 */
public class MockedAnnotation
        extends AnnotationImpl implements Annotation {

    public static final String INSTANCE = "inst";

    /**
     * Use {@link Builder} to construct your {@link Annotation} instead
     */
    MockedAnnotation(Integer id, Node start, Node end, String type, FeatureMap features) {
        super(id, start, end, type, features);
    }

    /**
     * Get {@link Builder} to help build the mocked {@link Annotation}
     *
     * @return the builder
     */
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MockedAnnotation that = (MockedAnnotation) o;

        if (this.getStartNode().getOffset() != that.getStartNode().getOffset()) return false;
        if (this.getEndNode().getOffset() != that.getEndNode().getOffset()) return false;
        if (!this.getType().equals(that.getType())) return false;
        return this.getFeatures().equals(that.getFeatures());

    }

    @Override
    public int hashCode() {
        String type = this.getType();
        long startOffset = this.getStartNode().getOffset();
        long endOffset = this.getEndNode().getOffset();
        int result = type.hashCode();
        result = 31 * result + (int) (startOffset ^ (startOffset >>> 32));
        result = 31 * result + (int) (endOffset ^ (endOffset >>> 32));
        result = 31 * result + this.getFeatures().hashCode();
        return result;
    }

    /**
     * Builder to help construct your {@link Annotation}
     */
    public static class Builder {
        private String inst;
        private String type;
        private Long startOff;
        private Long endOff;
        private FeatureMap featureMap = Factory.newFeatureMap();

        private Builder() {
        }

        /**
         * Optional <code>inst</code> instance value of the annotation
         *
         * @param inst the instance value
         * @return the builder
         */
        public Builder withInstance(String inst) {
            this.inst = inst;
            return this;
        }

        /**
         * <code>type</code> of the annotation
         *
         * @param type the type
         * @return the builder
         */
        public Builder withType(String type) {
            this.type = type;
            return this;
        }

        /**
         * <code>offset</code> of start {@link Node} and end node presenting the {@link Annotation} as GATE annotated from the input string or {@link Document}
         * <p>
         * Underneath, the builder helps creating {@link Node} implicitly
         *
         * @param startOff the start offset
         * @param endOff   the end offset
         * @return the builder
         */
        public Builder withOffset(long startOff, long endOff) {
            this.startOff = startOff;
            this.endOff = endOff;
            return this;
        }

        /**
         * Add feature by key and value into the annotation
         *
         * @param key   feature key
         * @param value feature value
         * @return the builder
         */
        public Builder withFeature(String key, Object value) {
            assert Optional.ofNullable(key).isPresent() : "Feature name is required";
            this.featureMap.put(key, value);
            return this;
        }

        /**
         * Helper to provide unique random integer as Id
         *
         * @return random number
         */
        private int random() {
            return new Random(System.nanoTime()).nextInt();
        }

        public Builder valueOf(Annotation annotation) {
            this.startOff = annotation.getStartNode().getOffset();
            this.endOff = annotation.getEndNode().getOffset();
            this.type = annotation.getType();
            this.inst = (String) annotation.getFeatures().get(INSTANCE);
            this.featureMap.clear();
            this.featureMap.putAll(annotation.getFeatures());
            return this;
        }

        /**
         * Build the mocked {@link Annotation}
         *
         * @return the annotation
         */
        public Annotation mock() {
            assert Optional.ofNullable(type).isPresent() : "Type is required";
            assert Optional.ofNullable(startOff).isPresent() : "Start offset is required";
            assert Optional.ofNullable(endOff).isPresent() : "End offset is required";

            featureMap.put(INSTANCE, inst);
            Node startNode = new NodeImpl(random(), startOff);
            Node endNode = new NodeImpl(random(), endOff);

            return new MockedAnnotation(random(), startNode, endNode, type, featureMap);
        }

    }
}
