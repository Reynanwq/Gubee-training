package br.com.gubee.interview.core.response;

import br.com.gubee.interview.model.Hero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeroFindResponse {
    private String message;
    private Hero data;
}
