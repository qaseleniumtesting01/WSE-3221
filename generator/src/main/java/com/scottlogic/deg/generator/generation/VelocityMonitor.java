package com.scottlogic.deg.generator.generation;

import com.scottlogic.deg.generator.Field;
import com.scottlogic.deg.generator.fieldspecs.FieldSpec;
import com.scottlogic.deg.generator.fieldspecs.RowSpec;
import com.scottlogic.deg.generator.outputs.GeneratedObject;
import com.scottlogic.deg.generator.walker.reductive.FixedField;
import com.scottlogic.deg.generator.walker.reductive.ReductiveState;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class VelocityMonitor implements ReductiveDataGeneratorMonitor {
    private String startedGenerating;
    private long rowsSinceLastSample;
    private BigInteger rowsEmitted;
    private BigInteger maxRows;
    private Timer timer = new Timer(true);
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");

    @Override
    public void generationStarting(GenerationConfig generationConfig) {
        this.startedGenerating = simpleDateFormat.format( new Date());
        this.rowsSinceLastSample = 0;
        this.rowsEmitted = BigInteger.ZERO;
        this.maxRows = BigInteger.valueOf(generationConfig.getMaxRows());

        System.out.println("Generation started at: " + this.startedGenerating + "\n");
        System.out.println("Number of rows | Current velocity (rows/sec)");
    }

    @Override
    public void rowEmitted(GeneratedObject row) {
        this.rowsSinceLastSample++;
        this.rowsEmitted = rowsEmitted.add(BigInteger.ONE);
    }

    @Override
    public void reportVelocity(long rowsSinceLastSample) {
        System.out.print(
        String.format(
            "%-14s | %d \r",
            this.rowsEmitted.toString(),
            rowsSinceLastSample)
        );
    }

    public void startTimer() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                reportVelocity(rowsSinceLastSample);
                rowsSinceLastSample = 0;
            }
        }, 1000L, 1000L);
    }

    @Override
    public void rowSpecEmitted(FixedField lastFixedField, FieldSpec fieldSpecForValuesInLastFixedField, RowSpec rowSpecWithAllValuesForLastFixedField) {

    }

    @Override
    public void fieldFixedToValue(Field field, Object current) {

    }

    @Override
    public void unableToStepFurther(ReductiveState reductiveState) {

    }

    @Override
    public void noValuesForField(ReductiveState reductiveState) {

    }

    @Override
    public void unableToEmitRowAsSomeFieldSpecsAreEmpty(ReductiveState reductiveState, Map<Field, FieldSpec> fieldSpecsPerField) {

    }
}
