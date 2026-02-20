//package br.com.gubee.interview.core.configuration;
//
//import br.com.gubee.interview.core.features.hero.HeroRepository;
//
//import br.com.gubee.interview.model.Hero;
//import br.com.gubee.interview.model.PowerStats;
//import br.com.gubee.interview.model.enums.Race;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.Arrays;
//
//@Configuration
//public class Instantiation implements CommandLineRunner {
//
//    @Autowired
//    private HeroRepository heroRepository;
//
//
//
//    @Override
//    public void run(String... args) throws Exception {
//
//        heroRepository.deleteAll();
//
//        // Create 3 PowerStats objects
//        PowerStats powerStats1 = new PowerStats( 100, 70, 80, 90);
//        PowerStats powerStats2 = new PowerStats( 80, 90, 75, 85);
//        PowerStats powerStats3 = new PowerStats( 85, 80, 90, 70);
//
//        Hero hero1 = new Hero(null,"Superman", Race.HUMAN, powerStats1, true);
//        Hero hero2 = new Hero(null,"Iron Man", Race.HUMAN, powerStats2, true);
//        Hero hero3 = new Hero(null,"Captain America", Race.CYBORG, powerStats3, true);
//
//        heroRepository.saveAll(Arrays.asList(hero1, hero2, hero3));
//
//    }
//}
