/*
 * Copyright 2019 Scott Logic Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.scottlogic.datahelix.generator.core.decisiontree.treepartitioning;

import com.scottlogic.datahelix.generator.common.profile.FieldBuilder;
import com.scottlogic.datahelix.generator.common.profile.Fields;
import com.scottlogic.datahelix.generator.common.whitelist.DistributedList;
import com.scottlogic.datahelix.generator.common.whitelist.WeightedElement;
import com.scottlogic.datahelix.generator.core.decisiontree.ConstraintNode;
import com.scottlogic.datahelix.generator.core.decisiontree.ConstraintNodeBuilder;
import com.scottlogic.datahelix.generator.core.decisiontree.DecisionNode;
import com.scottlogic.datahelix.generator.core.decisiontree.DecisionTree;
import com.scottlogic.datahelix.generator.core.decisiontree.testutils.*;
import com.scottlogic.datahelix.generator.core.profile.constraints.atomic.AtomicConstraint;
import com.scottlogic.datahelix.generator.core.profile.constraints.atomic.InSetConstraint;
import com.scottlogic.datahelix.generator.common.SetUtils;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.scottlogic.datahelix.generator.common.profile.FieldBuilder.createField;

class TreePartitionerTests {
    private static final ConstraintNode emptyConstraint
        = new ConstraintNodeBuilder().addAtomicConstraints(Collections.emptySet()).setDecisions(Collections.emptySet()).build();

    @Test
    void shouldSplitTreeIntoPartitions() {
        givenTree(
            tree(fields("A", "B", "C", "D", "E", "F"),
                constraint(
                    decision(
                        constraint("A"),
                        constraint("B")
                    ),
                    decision(
                        constraint("C"),
                        constraint("D")
                    ),
                    decision(
                        constraint("E"),
                        constraint("F")
                    )
        )));

        expectTrees(
            tree(fields("A", "B"),
                constraint(
                    decision(
                        constraint("A"),
                        constraint("B")
                    ))),
            tree(fields("C", "D"),
                constraint(
                    decision(
                        constraint("C"),
                        constraint("D")
                    ))),
            tree(fields("E", "F"),
                constraint(
                    decision(
                        constraint("E"),
                        constraint("F")
                    ))
        ));
    }

    @Test
    void shouldPartitionTwoNodesCorrectly() {
        givenTree(
            tree(fields("A", "B", "C", "D", "E", "F"),
                constraint(
                    decision(
                        constraint("A"),
                        constraint("B"),
                        constraint("E")
                    ),
                    decision(
                        constraint("C"),
                        constraint("D")
                    ),
                    decision(
                        constraint("E"),
                        constraint("F")
                    )
        )));

        expectTrees(
            tree(fields("A", "B", "E", "F"),
                constraint(
                    decision(
                        constraint("A"),
                        constraint("B"),
                        constraint("E")
                    ),
                    decision(
                        constraint("E"),
                        constraint("F")
                    ))),
            tree(fields("C", "D"),
                constraint(
                    decision(
                        constraint("C"),
                        constraint("D")
                    ))
            ));
    }

    @Test
    void shouldNotPartition() {
        givenTree(
            tree(fields("A", "B", "C", "D", "E", "F", "G"),
                constraint(
                    decision(
                        constraint("A"),
                        constraint("B"),
                        constraint("C")
                    ),
                    decision(
                        constraint("C"),
                        constraint("D"),
                        constraint("E")
                    ),
                    decision(
                        constraint("E"),
                        constraint("F"),
                        constraint("G")
                    )
        )));

        expectTrees(
            tree(fields("A", "B", "C", "D", "E", "F", "G"),
                constraint(
                    decision(
                        constraint("A"),
                        constraint("B"),
                        constraint("C")
                    ),
                    decision(
                        constraint("C"),
                        constraint("D"),
                        constraint("E")
                    ),
                    decision(
                        constraint("E"),
                        constraint("F"),
                        constraint("G")
                    )
                )
            ));
    }

    @Test
    void shouldPartitionConstraintsCorrectly() {
        givenTree(
            tree(fields("A", "B", "C"),
                constraint(
                    new String[] {"A", "B", "C"},
                    decision(constraint("A")),
                    decision(constraint("B")),
                    decision(constraint("C"))
                )
        ));

        expectTrees(
            tree(fields("A"),
                constraint(
                    new String[] {"A"},
                    decision(constraint("A"))
                )),
            tree(fields("B"),
                constraint(
                    new String[] {"B"},
                    decision(constraint("B"))
                )),
            tree(fields("C"),
                constraint(
                    new String[] {"C"},
                    decision(constraint("C"))
                ))
        );
    }

    @Test
    void shouldNotErrorIfFieldsNotConstrained() {
        givenTree(
            tree(fields("A", "B"),
                constraint("A")));

        expectTrees(
            tree(fields("A"),
                constraint("A")),
            tree(fields("B"),
                emptyConstraint));
    }

    @Test
    void shouldNotErrorIfNoFieldsConstrained() {
        givenTree(
            tree(fields("A", "B", "C"),
                emptyConstraint));

        expectTrees(
            tree(fields("A"), emptyConstraint),
            tree(fields("B"), emptyConstraint),
            tree(fields("C"), emptyConstraint));
    }

    @Test
    void simpleITwoPartitions() {
        givenTree(
            tree(fields("L", "T", "I", "E"),
                constraint(new String[]{"L", "T", "I", "E"},
                    decision(
                        constraint("T", "L"),
                        constraint("T")),
                    decision(
                        constraint("T", "L"),
                        constraint("T")),
                    decision(
                        constraint("I", "E"),
                        constraint("I")))));

        expectTrees(
            tree(fields("L", "T"),
                constraint(new String[]{"L", "T"},
                    decision(
                        constraint("T", "L"),
                        constraint("T")),
                    decision(
                        constraint("T", "L"),
                        constraint("T")))),
            tree(fields("I", "E"),
                constraint(new String[]{"I", "E"},
                    decision(
                        constraint("I", "E"),
                        constraint("I")))));
    }

    private ConstraintNode constraint(String... fieldNames) {
        return constraint(fieldNames, new DecisionNode[0]);
    }

    private ConstraintNode constraint(DecisionNode... decisions) {
        return constraint(new String[0], decisions);
    }

    private ConstraintNode constraint(String[] fieldNames, DecisionNode... decisions) {
        return new ConstraintNodeBuilder().addAtomicConstraints(Stream.of(fieldNames)
            .map(this::atomicConstraint)
            .collect(Collectors.toSet())).setDecisions(SetUtils.setOf(decisions)).build();
    }

    private AtomicConstraint atomicConstraint(String fieldName) {
        AtomicConstraint constraint = this.constraints.get(fieldName);

        if (constraint == null) {
            constraint = new InSetConstraint(
                createField(fieldName),
                new DistributedList<>(
                    Collections.singletonList(
                        new WeightedElement<>("sample-value", 1.0F))));
            this.constraints.put(fieldName, constraint);
        }

        return constraint;
    }

    private DecisionNode decision(ConstraintNode... constraints) {
        return new DecisionNode(constraints);
    }

    private Fields fields(String... fieldNames) {
        return new Fields(
            Stream.of(fieldNames)
                .map(FieldBuilder::createField)
                .collect(Collectors.toList()));
    }

    private DecisionTree tree(Fields fields, ConstraintNode rootNode) {
        return new DecisionTree(rootNode, fields);
    }

    @BeforeEach
    void beforeEach() {
        constraints = new HashMap<>();
        decisionTree = null;
        partitionedTrees = null;
    }

    private Map<String, AtomicConstraint> constraints;
    private List<DecisionTree> partitionedTrees;
    private DecisionTree decisionTree;

    private void givenTree(DecisionTree decisionTree) {
        this.decisionTree = decisionTree;
    }

    private void partitionTrees() {
        partitionedTrees = new TreePartitioner()
            .splitTreeIntoPartitions(decisionTree)
            .collect(Collectors.toList());
    }
    private void expectTrees(DecisionTree... decisionTrees) {
        if (partitionedTrees == null)
            partitionTrees();

        TreeComparisonReporter reporter = new TreeComparisonReporter();
        TreeComparisonContext context = new TreeComparisonContext();
        AnyOrderCollectionEqualityComparer defaultAnyOrderCollectionEqualityComparer = new AnyOrderCollectionEqualityComparer();
        EqualityComparer anyOrderComparer = new AnyOrderCollectionEqualityComparer(
            new TreeComparer(
                new ConstraintNodeComparer(
                    context,
                    defaultAnyOrderCollectionEqualityComparer,
                    new DecisionComparer(),
                    defaultAnyOrderCollectionEqualityComparer,
                    new AnyOrderCollectionEqualityComparer(new DecisionComparer())),
                new ProfileFieldComparer(context, defaultAnyOrderCollectionEqualityComparer, defaultAnyOrderCollectionEqualityComparer),
                context
            )
        );

        boolean match = anyOrderComparer.equals(
            partitionedTrees,
            Arrays.asList(decisionTrees));

        if (!match) {
            reporter.reportMessages(context);
            Assert.fail("Trees do not match");
        }
    }

}
