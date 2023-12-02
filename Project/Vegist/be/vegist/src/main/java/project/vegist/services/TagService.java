package project.vegist.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import project.vegist.dtos.TagDTO;
import project.vegist.entities.Tag;
import project.vegist.exceptions.ResourceExistException;
import project.vegist.repositories.TagRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagService {
    @Autowired
    private TagRepository tagRepository;

    public List<TagDTO> findAll() {
        List<Tag> tags = tagRepository.findAll();

        return tags.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public TagDTO findById(Long id) {
        return tagRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    public TagDTO create(TagDTO tagDTO) {
        Tag tag = new Tag(tagDTO.getName(), tagDTO.isStatus());
        Tag savedTag = tagRepository.save(tag);
        return convertToDTO(savedTag);
    }

    public TagDTO update(Long id, TagDTO updatedTagDTO) {
        if (!tagRepository.existsById(id)) {
            return null;
        }

        Tag existingTag = tagRepository.getById(id);
        existingTag.setTagName(updatedTagDTO.getName());
        existingTag.setStatus(updatedTagDTO.isStatus());

        Tag updatedTag = tagRepository.save(existingTag);
        return convertToDTO(updatedTag);
    }

    public boolean delete(Long id) {
        if (!tagRepository.existsById(id)) {
            return false;
        }

        tagRepository.deleteById(id);
        return true;
    }

    private TagDTO convertToDTO(Tag tag) {
        return new TagDTO(tag.getId(), tag.getTagName(), tag.getStatus());
    }

    public void validateTagName(String tagName) {
        if (tagRepository.existsByTagName(tagName)) {
            throw new ResourceExistException(tagName, HttpStatus.CONFLICT);
        }
    }

}
