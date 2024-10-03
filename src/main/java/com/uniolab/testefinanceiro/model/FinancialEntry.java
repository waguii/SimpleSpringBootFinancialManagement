package com.uniolab.testefinanceiro.model;

import com.uniolab.testefinanceiro.enums.FinancialEntryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class FinancialEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    private BigDecimal value;
    @Enumerated(EnumType.STRING)
    private FinancialEntryType type;
    private LocalDateTime date;
    private LocalDateTime registrationDate;
    private LocalDateTime competenceDate;
    @ManyToOne
    @JoinColumn(name = "financial_account_id", referencedColumnName = "id")
    private FinancialAccount financialAccount;
    @ManyToOne
    @JoinColumn(name = "transfer_account_id", referencedColumnName = "id")
    private FinancialAccount transferAccount;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        FinancialEntry that = (FinancialEntry) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
