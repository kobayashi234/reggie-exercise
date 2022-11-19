package com.demo.reggie.dto;


import com.demo.reggie.pojo.Setmeal;
import com.demo.reggie.pojo.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
