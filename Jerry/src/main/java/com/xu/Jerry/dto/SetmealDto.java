package com.xu.Jerry.dto;

import com.xu.Jerry.entity.Setmeal;
import com.xu.Jerry.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
