package cl.usach.mingesopep1.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "summary")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SummaryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private String date;
    private int code;
    private String name;
    private int kgsMilk;
    private int days;
    private float avgDailyMilk;
    private float milkVariation;
    private float fat;
    private float fatVariation;
    private float totalSolids;
    private float totalSolidsVariation;
    private float milkPayment;
    private float fatPayment;
    private float totalSolidsPayment;
    private float frenquencyBonus;
    private float milkVarDiscount;
    private float fatVarDiscount;
    private float stVarDiscount;
    private float totalPayment;
    private float retentionAmmount;
    private float finalPayment;
}
