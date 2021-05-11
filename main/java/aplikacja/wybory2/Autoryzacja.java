package aplikacja.wybory2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class Autoryzacja extends WebSecurityConfigurerAdapter {

    @Bean
    public UserDetailsService userDetailsService() {

        UserDetails wyb1 = User.withDefaultPasswordEncoder()
                .username("aaa")
                .password("111")
                .roles("USER")
                .build();
        UserDetails wyb2 = User.withDefaultPasswordEncoder()
                .username("bbb")
                .password("111")
                .roles("ADMIN")
                .build();
        UserDetails wyb3 = User.withDefaultPasswordEncoder()
                .username("ccc")
                .password("111")
                .roles("ADMIN", "USER")
                .build();
        return new InMemoryUserDetailsManager(wyb1, wyb2, wyb3);
    }

    /**
     * Url przy wylogowaniu to start2, żeby były zapamiętane aktualne informacje
     * o kandydowaniu, ilości głosów oraz o tym, kto już zagłosował.
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/", "/start2", "/przekieruj_glosuj", "/przekieruj_glosuj_nie",
                        "/zaglosuj", "/dolicz", "/dolicz_nie", "/zmien_haslo", "/zatwierdz_zmiane_hasla",
                        "/zobacz_frekwencje", "/wynik").permitAll()
                .antMatchers("/lista", "/dodaj", "/kasuj", "/wyszukaj",
                        "/aktualizuj", "/przekieriuj", "wylon", "/zarzadzWybory",
                        "/odwolajWybory", "/termin").hasAnyRole("ADMIN")
                .anyRequest().hasRole("USER")
                .and().logout().logoutSuccessUrl("/start2")
                .and().formLogin().permitAll()
                .defaultSuccessUrl("/home");
    }
}
