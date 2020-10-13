package hyve.petshow.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import hyve.petshow.domain.Avaliacao;
import hyve.petshow.exceptions.NotFoundException;
import hyve.petshow.mock.entidades.AvaliacaoMock;
import hyve.petshow.mock.repositorio.AvaliacaoRepositoryMock;
import hyve.petshow.repository.AvaliacaoRepository;
import hyve.petshow.service.implementation.AvaliacaoServiceImpl;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class AvaliacaoServiceTest {
	@Mock
	private AvaliacaoRepository repository;

	@InjectMocks
	private AvaliacaoServiceImpl service;

	@BeforeEach
	public void init() {
		initMocks(this);
		when(repository.findByServicoAvaliadoId(any(Long.class)))
				.then(mock -> AvaliacaoRepositoryMock.findByServicoAvaliado(mock.getArgument(0)));

		when(repository.save(any(Avaliacao.class))).then(mock -> AvaliacaoRepositoryMock.save(mock.getArgument(0)));
		doAnswer(mock -> {
			AvaliacaoRepositoryMock.limpaLista();
			return null;
		}).when(repository).deleteAll();
		when(repository.findAll()).thenReturn(AvaliacaoRepositoryMock.findAll());
	}

	@AfterEach
	public void limpa() {
		repository.deleteAll();
	}

	@Test
	public void deve_inserir_elemento_na_lista() {
		// dado
		var avaliacao = AvaliacaoMock.geraAvaliacao();

		// Quando
		var avaliacaoSalva = service.adicionarAvaliacao(avaliacao);

		// Entao
		assertTrue(repository.findAll().stream().anyMatch(el -> el.getId().equals(avaliacaoSalva.getId())));
	}

	@Test
	public void deve_retornar_lista_contendo_elemento() throws NotFoundException {
		// Dado
		var avaliacao = AvaliacaoMock.geraAvaliacao();
		var avaliacaoSalva = service.adicionarAvaliacao(avaliacao);

		// Quando
		var avaliacoes = service.buscarAvaliacoesPorServicoId(avaliacaoSalva.getServicoAvaliadoId());

		// Entao
		assertTrue(avaliacoes.stream().anyMatch(el -> el.getId().equals(avaliacaoSalva.getId())));
	}

	@Test
	public void deve_retornar_lista_de_avaliacoes() throws NotFoundException {
		// Dado
		var avaliacoes = AvaliacaoMock.geraListaAvaliacao().stream().map(avaliacao -> {
			return service.adicionarAvaliacao(avaliacao);
		}).collect(Collectors.toList());

		// Quando
		var servicoAvaliado = avaliacoes.get(0).getServicoAvaliadoId();
		var busca = service.buscarAvaliacoesPorServicoId(servicoAvaliado);

		// Então
		assertEquals(avaliacoes.size(), busca.size());

	}
	
	@Test
	public void deve_retornar_excecao_de_nenhuma_encontrada_por_servico() {
		Mockito.when(repository.findByServicoAvaliadoId(Mockito.anyLong())).thenReturn(new ArrayList<>());
		assertThrows(NotFoundException.class, () -> {
			service.buscarAvaliacoesPorServicoId(10l);
		});
	}
	
	@Test
	public void deve_retornar_algo_por_id() throws NotFoundException {
		Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.of(new Avaliacao()));
		assertNotNull(service.buscarAvaliacaoPorId(1l));
	}
	
	@Test
	public void deve_retornar_excecao_por_nao_encontrar_por_id() {
		Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
		
		assertThrows(NotFoundException.class, () -> {
			service.buscarAvaliacaoPorId(1l);
		});
	}
	
	

}
