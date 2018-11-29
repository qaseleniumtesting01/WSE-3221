package com.scottlogic.deg.generator.decisiontree.reductive;

import com.scottlogic.deg.generator.constraints.IConstraint;
import com.scottlogic.deg.generator.decisiontree.ConstraintNode;
import com.scottlogic.deg.generator.decisiontree.DecisionNode;
import com.scottlogic.deg.generator.decisiontree.NodeMarking;
import com.scottlogic.deg.generator.restrictions.RowSpec;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;

public class ReductiveConstraintNode implements ConstraintNode {
    private final ConstraintNode underlying;
    private final Collection<IConstraint> unfixedAtomicConstraints;

    public ReductiveConstraintNode(ConstraintNode underlying, Collection<IConstraint> unfixedAtomicConstraints) {
        this.underlying = underlying;
        this.unfixedAtomicConstraints = unfixedAtomicConstraints;
    }

    @Override
    public Collection<IConstraint> getAtomicConstraints() {
        return underlying.getAtomicConstraints();
    }

    @Override
    public Collection<DecisionNode> getDecisions() {
        return underlying.getDecisions();
    }

    @Override
    public Optional<RowSpec> getOrCreateRowSpec(Supplier<Optional<RowSpec>> createRowSpecFunc) {
        return underlying.getOrCreateRowSpec(createRowSpecFunc);
    }

    @Override
    public ConstraintNode removeDecisions(Collection<DecisionNode> decisionsToRemove) {
        return new ReductiveConstraintNode(
            underlying.removeDecisions(decisionsToRemove),
            this.unfixedAtomicConstraints);
    }

    @Override
    public ConstraintNode cloneWithoutAtomicConstraint(IConstraint excludeAtomicConstraint) {
        return new ReductiveConstraintNode(
            underlying.cloneWithoutAtomicConstraint(excludeAtomicConstraint),
            this.unfixedAtomicConstraints);
    }

    @Override
    public boolean atomicConstraintExists(IConstraint constraint) {
        return underlying.atomicConstraintExists(constraint);
    }

    @Override
    public ConstraintNode addAtomicConstraints(Collection<IConstraint> constraints) {
        return new ReductiveConstraintNode(
            underlying.addAtomicConstraints(constraints),
            this.unfixedAtomicConstraints);
    }

    @Override
    public ConstraintNode addDecisions(Collection<DecisionNode> decisions) {
        return new ReductiveConstraintNode(
            underlying.addDecisions(decisions),
            this.unfixedAtomicConstraints);
    }

    @Override
    public ConstraintNode setDecisions(Collection<DecisionNode> decisions) {
        return new ReductiveConstraintNode(
            underlying.setDecisions(decisions),
            this.unfixedAtomicConstraints);
    }

    @Override
    public String toString() {
        return underlying.toString();
    }

    public Collection<IConstraint> getAllUnfixedAtomicConstraints(){
        return this.unfixedAtomicConstraints;
    }

    @Override
    public ConstraintNode markNode(NodeMarking marking) {
        return new ReductiveConstraintNode(
            underlying.markNode(marking),
            this.unfixedAtomicConstraints
        );
    }

    @Override
    public boolean hasMarking(NodeMarking detail) {
        return underlying.hasMarking(detail);
    }
}
