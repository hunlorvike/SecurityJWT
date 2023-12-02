package project.vegist.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.vegist.dtos.RoleDTO;
import project.vegist.entities.Role;
import project.vegist.repositories.RoleRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public List<RoleDTO> findAll() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<RoleDTO> findById(Long id) {
        return roleRepository.findById(id)
                .map(this::convertToDTO);
    }

    public RoleDTO create(RoleDTO roleDTO) {
        Role role = new Role();
        role.setRoleName(roleDTO.getName());

        Role savedRole = roleRepository.save(role);
        return convertToDTO(savedRole);
    }

    public Optional<RoleDTO> update(Long id, RoleDTO roleDTO) {
        Optional<Role> existingRoleOptional = roleRepository.findById(id);

        if (existingRoleOptional.isPresent()) {
            Role existingRole = existingRoleOptional.get();
            existingRole.setRoleName(roleDTO.getName());

            Role updatedRole = roleRepository.save(existingRole);
            return Optional.ofNullable(convertToDTO(updatedRole));
        }

        return Optional.empty();
    }

    public boolean delete(Long id) {
        Optional<Role> roleOptional = roleRepository.findById(id);

        if (roleOptional.isPresent()) {
            roleRepository.deleteById(id);
            return true; // Deleted successfully
        }

        return false; // Handle not found scenario
    }

    public boolean exists(String name) {
        return roleRepository.findByRoleName(name).isPresent();
    }

    // Helper method to convert Role to RoleDTO
    private RoleDTO convertToDTO(Role role) {
        return new RoleDTO(role.getId(), role.getRoleName());
    }
}
