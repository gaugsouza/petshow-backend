package hyve.petshow.controller.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import hyve.petshow.controller.representation.PrestadorRepresentation;
import hyve.petshow.domain.Prestador;
import hyve.petshow.domain.enums.TipoConta;

// TODO: IDEM ClienteCONVERTER
@Component // coloca a entidade dentro do contexto Spring
public class PrestadorConverter implements Converter<Prestador, PrestadorRepresentation> {
    //private ContaConverter contaConverter = new ContaConverter();
    //private List<ServicoDetalhado> servicoDetalhadoConverter = new ArrayList<ServicoDetalhado>();
    private String descricao;

    @Override
    public PrestadorRepresentation toRepresentation(Prestador domain) {
        if(domain == null) return new PrestadorRepresentation();
//		PrestadorRepresentation representation = (PrestadorRepresentation) contaConverter.toRepresentation(domain);
        PrestadorRepresentation representation = new PrestadorRepresentation();
        representation.setNomeSocial(domain.getNomeSocial());

        representation.setCpf(domain.getCpf());
        representation.setEndereco(domain.getEndereco());
        representation.setFoto(domain.getFoto());
        representation.setId(domain.getId());
        representation.setLogin(domain.getLogin());
		representation.setNome(domain.getNome());
		representation.setNomeSocial(domain.getNomeSocial());
		representation.setTelefone(domain.getTelefone());
		representation.setTipo(domain.getTipo() == null ? null : domain.getTipo().getTipo());
        //domain.setServicoDetalhado(servicoDetalhadoConverter.toDomainList(representation.getServicoDetalhado()));
		representation.setDescricao(domain.getDescricao());
        return representation;
    }

    @Override
    public Prestador toDomain(PrestadorRepresentation representation) {
        if(representation == null) return new Prestador();
//		Prestador domain = (Prestador) contaConverter.toDomain(representation);
        Prestador domain = new Prestador();
        domain.setNomeSocial(representation.getNomeSocial());

        domain.setCpf(representation.getCpf());
		domain.setEndereco(representation.getEndereco());
		domain.setFoto(representation.getFoto());
		domain.setId(representation.getId());
		domain.setLogin(representation.getLogin());
		domain.setNome(representation.getNome());
		domain.setNomeSocial(representation.getNomeSocial());
		domain.setTelefone(representation.getTelefone());
		domain.setTipo(TipoConta.getTipoByInteger(representation.getTipo()));
        //domain.setServicoDetalhado(servicoDetalhadoConverter.toDomainList(representation.getServicoDetalhado()));
        domain.setDescricao(representation.getDescricao());
        return domain;
    }

    public List<PrestadorRepresentation> toRepresentationList(List<Prestador> domainList){
        if(domainList == null) return new ArrayList<PrestadorRepresentation>();
        List<PrestadorRepresentation> representationList = new ArrayList<>();

        domainList.forEach(domain -> representationList.add(this.toRepresentation(domain)));
        return representationList;
    }

    public List<Prestador> toDomainList(List<PrestadorRepresentation> prestador) {
        if(prestador == null) return new ArrayList<Prestador>();
        return prestador.stream().map(el -> toDomain(el)).collect(Collectors.toList());
    }


}