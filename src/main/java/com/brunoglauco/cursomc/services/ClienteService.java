package com.brunoglauco.cursomc.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.brunoglauco.cursomc.domain.Cidade;
import com.brunoglauco.cursomc.domain.Cliente;
import com.brunoglauco.cursomc.domain.Endereco;
import com.brunoglauco.cursomc.domain.enums.Perfil;
import com.brunoglauco.cursomc.domain.enums.TipoCliente;
import com.brunoglauco.cursomc.dto.ClienteDTO;
import com.brunoglauco.cursomc.dto.ClienteNewDTO;
import com.brunoglauco.cursomc.repositories.CidadeRepository;
import com.brunoglauco.cursomc.repositories.ClienteRepository;
import com.brunoglauco.cursomc.repositories.EnderecoRepository;
import com.brunoglauco.cursomc.security.UserSS;
import com.brunoglauco.cursomc.services.exceptions.AuthorizationException;
import com.brunoglauco.cursomc.services.exceptions.DataIntegrityException;
import com.brunoglauco.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class ClienteService {

	@Autowired
	private BCryptPasswordEncoder pe;
	
	@Autowired
	private ClienteRepository repo;

	@Autowired
	private EnderecoRepository enderecoRepository;

	@Autowired
	private CidadeRepository cidadeRepository;

	public Cliente find(Integer id) {
		
		UserSS user = UserService.authenticated();
		if (user==null || !user.hasRole(Perfil.ADMIN) && !id.equals(user.getId())) {
			throw new AuthorizationException("Acesso negado");
		}
		
		Optional<Cliente> obj = repo.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + id + ", Tipo: " + Cliente.class.getName()));
	}

	@Transactional
	public Cliente insert(Cliente obj) {
		obj.setId(null);
		obj = repo.save(obj);
		enderecoRepository.saveAll(obj.getEnderecos());
		return obj;
	}

	public Cliente update(Cliente obj) {
		Cliente newObj = find(obj.getId());
		updateData(newObj, obj);
		return repo.save(newObj);
	}

	public void delete(Integer id) {
		find(id);
		try {
			repo.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possível excluir porque a entidades relacionadas");
		}
	}

	public List<Cliente> findAll() {
		return repo.findAll();
	}

	public Page<Cliente> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		return repo.findAll(pageRequest);
	}

	private void updateData(Cliente newObj, Cliente obj) {
		newObj.setNome(obj.getNome());
		newObj.setEmail(obj.getEmail());
	}

	public Cliente fromDTO(ClienteDTO objDTO) {
		return new Cliente(objDTO.getId(), objDTO.getNome(), objDTO.getEmail(), null, null, null);
	}

	public Cliente fromDTO(ClienteNewDTO objDTO) {
		Cliente cli = new Cliente(null, objDTO.getNome(), objDTO.getEmail(), objDTO.getCpfOuCnpj(),
				TipoCliente.toEnum(objDTO.getTipo()),pe.encode(objDTO.getSenha()));

		Cidade cid = new Cidade();
		cid.setId(objDTO.getCidadeId());

		Endereco end = new Endereco(null, objDTO.getLogradouro(), objDTO.getNumero(), objDTO.getComplemento(),
				objDTO.getBairro(), objDTO.getCep(), cli, cid);

		cli.getEnderecos().add(end);
		cli.getTelefones().add(objDTO.getTelefone1());
		if (objDTO.getTelefone2()!= null) {
			cli.getTelefones().add(objDTO.getTelefone2());
		}
		if (objDTO.getTelefone3()!= null) {
			cli.getTelefones().add(objDTO.getTelefone3());
		}

		return cli;
	}

}
