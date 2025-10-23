package tw.fengqing.spring.springbucks.customer.model;

import java.io.Serializable;
import java.util.Date;

import org.joda.money.Money;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coffee implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private Money price;
    private Date createTime;
    private Date updateTime;
}
