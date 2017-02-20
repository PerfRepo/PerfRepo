package org.perfrepo.web.adapter.dummy_impl.storage;

import org.perfrepo.dto.test_execution.AttachmentDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Temporary in-memory test execution attachment storage for development purpose.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class AttachmentStorage {

    private Long key = 1L;
    private List<AttachmentDto> data = new ArrayList<>();

    public AttachmentDto getById(Long id) {
        Optional<AttachmentDto> attachment = data.stream().filter(dto -> dto.getId().equals(id)).findFirst();
        return attachment.isPresent() ? attachment.get() : null;
    }

    public AttachmentDto create(AttachmentDto dto) {
        dto.setId(getNextId());
        data.add(dto);
        return dto;
    }

    public boolean delete(Long id) {
        return data.removeIf(attachment -> attachment.getId().equals(id));
    }

    private Long getNextId() {
        return key++;
    }
}