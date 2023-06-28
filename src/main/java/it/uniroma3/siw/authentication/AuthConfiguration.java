package it.uniroma3.siw.authentication;

import static it.uniroma3.siw.model.Credentials.ADMIN_ROLE;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
	public class AuthConfiguration  {

    @Autowired
    private DataSource dataSource;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth)
            throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .authoritiesByUsernameQuery("SELECT username, role from credentials WHERE username=?")
                .usersByUsernameQuery("SELECT username, password, 1 as enabled FROM credentials WHERE username=?");
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

  @Bean
  PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
  }
    
    @Bean
    protected SecurityFilterChain configure(final HttpSecurity httpSecurity) throws Exception{
        httpSecurity
                .csrf().and().cors().disable()
                .authorizeHttpRequests()
//                .requestMatchers("/**").permitAll()
                // chiunque (autenticato o no) può accedere alle pagine index, login, register, ai css e alle immagini
                .requestMatchers(HttpMethod.GET,
                		"/","/index","/register",
                		"/indexMovie", "/movies", "/movie/{id}", "/formSearchMovies", 
                		"/indexArtist", "/artists", "/artist/{id}", "/formSearchArtists",
                		"/css/**", "/images/**", "favicon.ico").permitAll()
        		// chiunque (autenticato o no) può mandare richieste POST al punto di accesso per login e register
                .requestMatchers(HttpMethod.POST,
                		"/register", "/login",
                		"/searchMovies",
                		"/searchArtists").permitAll()
                .requestMatchers(HttpMethod.GET,"/admin/**").hasAnyAuthority(ADMIN_ROLE)
                .requestMatchers(HttpMethod.POST,"/admin/**").hasAnyAuthority(ADMIN_ROLE)
        		// tutti gli utenti autenticati possono accere alle pagine rimanenti
                .anyRequest().authenticated()
                // LOGIN: qui definiamo il login
                .and().formLogin()
                .loginPage("/login")
                .permitAll()
                .defaultSuccessUrl("/success", true)
                .failureUrl("/login?error=true")
                // LOGOUT: qui definiamo il logout
                .and()
                .logout()
                // il logout è attivato con una richiesta GET a "/logout"
                .logoutUrl("/logout")
                // in caso di successo, si viene reindirizzati alla home
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .clearAuthentication(true).permitAll();
        return httpSecurity.build();
    }
}

//package it.uniroma3.siw.authentication;
//
//import static it.uniroma3.siw.model.Credentials.ADMIN_ROLE;
//
//import javax.sql.DataSource;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;;
//
//@Configuration
//@EnableWebSecurity
//public class AuthConfiguration extends WebSecurityConfigurerAdapter {
//
//    /**
//     * La sorgente dati (che contiene le credenziali) è
//     * iniettata automaticamente
//     */
//    @Autowired
//    DataSource datasource;
//
//    /**
//     * Questo metodo contiene le impostazioni della configurazione
//     * di autenticatzione e autorizzazione.
//     */
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                // AUTORIZZAZIONE: qui definiamo chi può accedere a cosa
//                .authorizeRequests()
//                // chiunque (autenticato o no) può accedere alle pagine index, login, register, ai css e alle immagini
//                .antMatchers(HttpMethod.GET, "/", "/index", "/login", "/register", "/css/**", "/images/**", "favicon.ico").permitAll()
//                // chiunque (autenticato o no) può mandare richieste POST al punto di accesso per login e register
//                .antMatchers(HttpMethod.POST, "/login", "/register").permitAll()
//                // solo gli utenti autenticati con ruolo ADMIN possono accedere a risorse con path /admin/**
//                .antMatchers(HttpMethod.GET, "/admin/**").hasAnyAuthority(ADMIN_ROLE)
//                .antMatchers(HttpMethod.POST, "/admin/**").hasAnyAuthority(ADMIN_ROLE)
//                // tutti gli utenti autenticati possono accere alle pagine rimanenti
//                .anyRequest().authenticated()
//                .and().exceptionHandling().accessDeniedPage("/index")
//
//                // LOGIN: qui definiamo come è gestita l'autenticazione
//                // usiamo il protocollo formlogin
//                .and().formLogin()
//                // la pagina di login si trova a /login
//                .loginPage("/login")
//                // se il login ha successo, si viene rediretti al path /default
//                .defaultSuccessUrl("/success", true)
//
//                // LOGOUT: qui definiamo il logout
//                .and()
//                .logout()
//                // il logout è attivato con una richiesta GET a "/logout"
//                .logoutUrl("/logout")
//                // in caso di successo, si viene reindirizzati alla home
//                .logoutSuccessUrl("/")
//                .invalidateHttpSession(true)
//                .deleteCookies("JSESSIONID")
//                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
//                .clearAuthentication(true).permitAll();
//    }
//
//    /**
//     * Questo metodo definisce le query SQL per ottenere username e password
//     */
//    @Override
//    public void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.jdbcAuthentication()
//                .dataSource(this.datasource)
//                //query per recuperare username e ruolo
//                .authoritiesByUsernameQuery("SELECT username, ruolo FROM credentials WHERE username=?")
//                //query per username e password. Il flag boolean flag specifica se l'utente user è abilitato o no (va sempre a true)
//                .usersByUsernameQuery("SELECT username, password, 1 as enabled FROM credentials WHERE username=?");
//    }
//
//    /**
//     * Questo metodo definisce il componente "passwordEncoder",
//     * usato per criptare e decriptare la password nella sorgente dati.
//     */
//    @Bean
//    PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}
