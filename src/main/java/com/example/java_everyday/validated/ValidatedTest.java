package com.example.java_everyday.validated;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
class BigPeople {

    @NotNull(message = "id不能为空")
    @Max(message = "id不能超过{value}个长度", value = 5)
    private Integer id;

    @NotBlank(message = "name不能为空")
    @Size(message = "名字最长为{max} 个字", max = 3)
    private String name;

    @NotNull(message = "age不能为空")
    @Range(message = "age的长度范围为{min}岁到{max}岁之间", min = 5, max = 10)
    private Integer age;

    @Email
    private String email;

    @Future(message = "必须是未来的日期")
    private Date future;

    @Past(message = "必须是过去的日期")
    private Date past;

    @Override
    public String toString() {
        return "BigPeople{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}


@RestController
@RequestMapping("/valid")
public class ValidatedTest {

    @PostMapping("/addOne")
    public String updateOrInsert(@Validated @RequestBody BigPeople person, BindingResult res) {

        System.out.println(person);
        if (res.hasErrors()) {
            List<String> resList = new ArrayList<>(10);
            res.getAllErrors().forEach(e -> resList.add(e.getDefaultMessage()));

            return String.join(", ", resList);
        }
        return "success";
    }

}