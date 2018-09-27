package com.scottlogic.deg.generator.generation;

import com.scottlogic.deg.generator.Field;
import com.scottlogic.deg.generator.constraints.IConstraint;
import com.scottlogic.deg.generator.decisiontree.ConstraintNode;
import com.scottlogic.deg.generator.decisiontree.DecisionNode;
import com.scottlogic.deg.generator.utils.ConcatenatingIterable;
import com.scottlogic.deg.generator.utils.ProjectingIterable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Given a profile and mapping from rules to fields, split the tree into multiple trees based on which rules affect which fields
 */
public class TreePartitioner {
    public Stream<PartitionTree> splitTreeIntoPartitions(ConstraintNode rootNode, Map<Object, Set<Field>> mapping) {
        final Map<Field, Integer> partitionsByField = new HashMap<>();
        final Map<Integer, Set<Field>> partitionsById = new HashMap<>();

        int partitionCount = 0;

        ConcatenatingIterable<Object> fieldedObjects = new ConcatenatingIterable<>(
            new ProjectingIterable<>(rootNode.getAtomicConstraints(), constraint -> constraint),
            new ProjectingIterable<>(rootNode.getDecisions(), decision -> decision)
        );

        for (Object fieldedObject : fieldedObjects) {
            final Set<Field> fields = mapping.get(fieldedObject);

            final List<Integer> partitionsTouched = fields
                .stream()
                .map(partitionsByField::get)
                .distinct()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

            final Set<Field> fieldsToPartition = partitionsTouched.size() == 0
                ? fields
                : Stream.concat(
                    fields.stream(),
                    partitionsTouched
                        .stream()
                        .flatMap(id -> partitionsById.get(id).stream()))
                    .collect(Collectors.toSet());

            final int currentPartition = partitionsTouched.size() == 1
                ? partitionsTouched.get(0)
                : partitionCount++;

            partitionsById.put(currentPartition, fieldsToPartition);
            fieldsToPartition.forEach(field -> partitionsByField.put(field, currentPartition));
        }

        Map<Integer, List<IConstraint>> partitionedConstraints = rootNode
            .getAtomicConstraints()
            .stream()
            .collect(Collectors.groupingBy(constraint -> partitionsByField.get(mapping.get(constraint).stream().findFirst().get())));

        Map<Integer, List<DecisionNode>> partitionedDecisions = rootNode
            .getDecisions()
            .stream()
            .collect(Collectors.groupingBy(decision -> partitionsByField.get(mapping.get(decision).stream().findFirst().get())));

        return partitionsById.size() == 0
            ? Stream.of(new PartitionTree(rootNode, mapping.get(rootNode)))
            : partitionsById
                .keySet()
                .stream()
                .map(id -> new PartitionTree(
                    new ConstraintNode( // TODO: consider moving this class to decision tree so we don't have to make that constructor public
                        partitionedConstraints.getOrDefault(id, Collections.emptyList()),
                        partitionedDecisions.getOrDefault(id, Collections.emptyList())
                    ),
                    partitionsById.get(id)
                ));
//            .map(id -> new ConstraintNode(
//                partitionedConstraints.getOrDefault(id, Collections.emptyList()),
//                partitionedDecisions.getOrDefault(id, Collections.emptyList())
//            ));

//        return partitionsById
//            .keySet()
//            .stream()
//            .map(partitionId -> {
//
//            });
    }
//            .reduce(
//                new RuleDecisionTree("", new ConstraintNode()),
//                (accumulator, nextRule) -> {
//                    return Stream.of(nextRule);
//                },
//                rules -> new DecisionTreeProfile(profile.getFields(), rules.collect(Collectors.toList())),
//                rules -> rules);




//    public Stream<DecisionTreeProfile> splitTreeIntoPartitionsOld(DecisionTreeProfile profile, Map<RuleDecisionTree, List<Field>> ruleFieldMapping) {
//
//        final Map<Field, Integer> partitionsByField = new HashMap<>();
//        final Map<Integer, List<Field>> partitionsById = new HashMap<>();
//
//        int partitionCount = 0;
//
//        for (RuleDecisionTree rule : profile.getDecisionTrees()) {
//            final List<Field>  fields = ruleFieldMapping.get(rule);
//
//            final List<Integer> partitionsTouched = fields
//                .stream()
//                .map(partitionsByField::get)
//                .distinct()
//                .collect(Collectors.toList());
//
//            final List<Field> fieldsToPartition = partitionsTouched.size() <= 1
//                ? fields
//                : Stream.concat(
//                        fields.stream(),
//                        partitionsTouched
//                            .stream()
//                            .flatMap(id -> partitionsById.get(id).stream()))
//                    .collect(Collectors.toList());
//
//            final int currentPartition = partitionsTouched.size() == 1
//                ? partitionsTouched.get(0)
//                : partitionCount++;
//
//            partitionsById.put(currentPartition, fieldsToPartition);
//            fieldsToPartition.forEach(field -> partitionsByField.put(field, currentPartition));
//        }
//
//        return null;
////            .reduce(
////                new RuleDecisionTree("", new ConstraintNode()),
////                (accumulator, nextRule) -> {
////                    return Stream.of(nextRule);
////                },
////                rules -> new DecisionTreeProfile(profile.getFields(), rules.collect(Collectors.toList())),
////                rules -> rules);
//    }
//}
}
