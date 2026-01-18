package br.com.gubee.interview.core.response;


import br.com.gubee.interview.model.Hero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeroListResponse {
    private String message;
    private int count;
    private List<Hero> data;
}