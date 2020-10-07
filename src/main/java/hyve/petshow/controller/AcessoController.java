package hyve.petshow.controller;

import hyve.petshow.controller.converter.ContaConverter;
import hyve.petshow.controller.representation.ContaRepresentation;
import hyve.petshow.domain.Conta;
import hyve.petshow.domain.Login;
import hyve.petshow.exceptions.BusinessException;
import hyve.petshow.exceptions.NotFoundException;
import hyve.petshow.service.port.AcessoService;
import hyve.petshow.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/acesso")
public class AcessoController {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private AcessoService acessoService;
    @Autowired
    private ContaConverter contaConverter;

    private final String mensagemErro = "Erro durante a autenticação, usuário ou senha incorretos.";

    @PostMapping("/login")
    public ResponseEntity<String> realizarLogin(@RequestBody Login login) throws NotFoundException, BusinessException {
        try {
            realizarAutenticacao(login);
            var token = gerarToken(login.getEmail());
            return ResponseEntity.ok(token);
        } catch (AuthenticationException e) {
            log.error("{}, mensagem: {}, causa: {}", mensagemErro, e.getMessage(), e.getCause());
            throw new BusinessException(mensagemErro);
        } catch (NotFoundException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    @PostMapping("/cadastro")
    public ResponseEntity<String> realizarCadastro(@RequestBody ContaRepresentation contaRepresentation) throws BusinessException {
        try {
            verificarEmailExistente(contaRepresentation.getLogin().getEmail());
            var conta = adicionarConta(contaRepresentation);
            var token = gerarToken(conta);

            return ResponseEntity.ok(token);
        } catch (AuthenticationException e) {
            log.error("{}, mensagem: {}, causa: {}", mensagemErro, e.getMessage(), e.getCause());
            throw new BusinessException(mensagemErro);
        }
    }

    private void realizarAutenticacao(Login login) throws AuthenticationException{
        var token = new UsernamePasswordAuthenticationToken(login.getEmail(), login.getSenha());
        authenticationManager.authenticate(token);
    }

    private String gerarToken(String email) throws NotFoundException {
        var conta = acessoService.buscarPorEmail(email)
                .orElseThrow(() -> new NotFoundException("Login informado não encontrado no sistema"));
        var token = jwtUtil.generateToken(email, conta.getId(), conta.getTipo());
        return token;
    }

    private String gerarToken(Conta conta) {
        var token = jwtUtil.generateToken(conta.getLogin().getEmail(), conta.getId(), conta.getTipo());
        return token;
    }

    private void verificarEmailExistente(String email) throws BusinessException {
        if(acessoService.buscarPorEmail(email).isPresent()){
            throw new BusinessException("Email já cadastrado no sistema");
        }
    }

    private Conta adicionarConta(ContaRepresentation contaRepresentation) throws BusinessException {
        var request = contaConverter.toDomain(contaRepresentation);
        var conta = acessoService.adicionarConta(request);
        return conta;
    }
}
