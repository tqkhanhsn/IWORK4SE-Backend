package vn.iwork4se.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.iwork4se.controller.request.SavedApplicantCreationRequest;
import vn.iwork4se.controller.response.SavedApplicantPageResponse;
import vn.iwork4se.controller.response.SavedApplicantResponse;
import vn.iwork4se.exception.ResourceNotFoundException;
import vn.iwork4se.model.Applicant;
import vn.iwork4se.model.SavedApplicant;
import vn.iwork4se.model.SavedApplicantList;
import vn.iwork4se.repository.ApplicantRepository;
import vn.iwork4se.repository.SavedApplicantRepository;
import vn.iwork4se.service.SavedApplicantListService;
import vn.iwork4se.service.SavedApplicantService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SavedApplicantServiceImpl implements SavedApplicantService {

    private final SavedApplicantRepository savedApplicantRepository;
    private final ApplicantRepository applicantRepository;
    private final SavedApplicantListService savedApplicantListService;

    @Override
    @Transactional
    public SavedApplicantResponse saveApplicant(SavedApplicantCreationRequest request) {
        log.info("Saving applicant to list: {}", request.getListId());

        // Check if applicant is already saved in this list
        Optional<SavedApplicant> existing = savedApplicantRepository
                .findBySavedApplicantListIdAndApplicantId(request.getListId(), request.getApplicantId());

        if (existing.isPresent()) {
            throw new RuntimeException("Applicant is already saved in this list");
        }

        SavedApplicantList list = savedApplicantListService.getListEntityById(request.getListId());
        Applicant applicant = applicantRepository.findById(request.getApplicantId())
                .orElseThrow(() -> new ResourceNotFoundException("Applicant not found"));

        SavedApplicant savedApplicant = SavedApplicant.builder()
                .id("SA" + UUID.randomUUID().toString())
                .savedApplicantList(list)
                .applicant(applicant)
                .isContacted(false)
                .notes(request.getNotes())
                .build();

        SavedApplicant saved = savedApplicantRepository.save(savedApplicant);

        // Update applicant's savedInListIds
        applicant.getSavedInListIds().add(list.getId());
        applicantRepository.save(applicant);

        log.info("Saved applicant successfully: {}", saved.getId());

        return convertToResponse(saved);
    }

    @Override
    public SavedApplicantPageResponse getApplicantsByList(String listId, int page, int size) {
        log.info("Getting saved applicants for list: {}", listId);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "savedDate"));
        Page<SavedApplicant> savedApplicantPage = savedApplicantRepository.findBySavedApplicantListId(listId, pageable);

        return convertToPageResponse(savedApplicantPage);
    }

    @Override
    public SavedApplicantPageResponse getApplicantsByListAndContactStatus(String listId, Boolean isContacted, int page, int size) {
        log.info("Getting saved applicants for list: {} with contact status: {}", listId, isContacted);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "savedDate"));
        Page<SavedApplicant> savedApplicantPage = savedApplicantRepository
                .findBySavedApplicantListIdAndIsContacted(listId, isContacted, pageable);

        return convertToPageResponse(savedApplicantPage);
    }

    @Override
    public SavedApplicantPageResponse getAllSavedApplicantsByEmployer(String employerId, int page, int size) {
        log.info("Getting all saved applicants for employer: {}", employerId);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "savedDate"));
        Page<SavedApplicant> savedApplicantPage = savedApplicantRepository.findByEmployerId(employerId, pageable);

        return convertToPageResponse(savedApplicantPage);
    }

    @Override
    @Transactional
    public SavedApplicantResponse updateContactStatus(String savedApplicantId, Boolean isContacted) {
        log.info("Updating contact status for saved applicant: {}", savedApplicantId);

        SavedApplicant savedApplicant = savedApplicantRepository.findById(savedApplicantId)
                .orElseThrow(() -> new ResourceNotFoundException("Saved applicant not found"));

        savedApplicant.setIsContacted(isContacted);
        SavedApplicant updated = savedApplicantRepository.save(savedApplicant);

        log.info("Updated contact status successfully");

        return convertToResponse(updated);
    }

    @Override
    @Transactional
    public SavedApplicantResponse updateNotes(String savedApplicantId, String notes) {
        log.info("Updating notes for saved applicant: {}", savedApplicantId);

        SavedApplicant savedApplicant = savedApplicantRepository.findById(savedApplicantId)
                .orElseThrow(() -> new ResourceNotFoundException("Saved applicant not found"));

        savedApplicant.setNotes(notes);
        SavedApplicant updated = savedApplicantRepository.save(savedApplicant);

        log.info("Updated notes successfully");

        return convertToResponse(updated);
    }

    @Override
    @Transactional
    public void removeApplicantFromList(String savedApplicantId) {
        log.info("Removing saved applicant: {}", savedApplicantId);

        SavedApplicant savedApplicant = savedApplicantRepository.findById(savedApplicantId)
                .orElseThrow(() -> new ResourceNotFoundException("Saved applicant not found"));

        String listId = savedApplicant.getSavedApplicantList().getId();
        String applicantId = savedApplicant.getApplicant().getId();

        savedApplicantRepository.delete(savedApplicant);

        Applicant applicant = applicantRepository.findById(applicantId).orElse(null);
        if (applicant != null) {
            applicant.getSavedInListIds().remove(listId);
            applicantRepository.save(applicant);
        }

        log.info("Removed saved applicant successfully");
    }

    @Override
    @Transactional
    public void removeApplicantFromListByIds(String listId, String applicantId) {
        log.info("Removing applicant {} from list {}", applicantId, listId);

        SavedApplicant savedApplicant = savedApplicantRepository
                .findBySavedApplicantListIdAndApplicantId(listId, applicantId)
                .orElseThrow(() -> new ResourceNotFoundException("Saved applicant not found in this list"));

        removeApplicantFromList(savedApplicant.getId());
    }

    @Override
    public boolean isApplicantSavedInList(String listId, String applicantId) {
        return savedApplicantRepository.findBySavedApplicantListIdAndApplicantId(listId, applicantId).isPresent();
    }

    private SavedApplicantResponse convertToResponse(SavedApplicant savedApplicant) {
        Applicant applicant = savedApplicant.getApplicant();

        return SavedApplicantResponse.builder()
                .id(savedApplicant.getId())
                .listId(savedApplicant.getSavedApplicantList().getId())
                .listName(savedApplicant.getSavedApplicantList().getListName())
                .applicantId(applicant.getId())
                .applicantName(applicant.getFirstName() + " " + applicant.getLastName())
                .applicantEmail(applicant.getEmail())
                .applicantPhone(applicant.getPhone())
                .applicantGender(applicant.getGender())
                .yearsOfExperience(applicant.getYearsOfExperience())
                .major(applicant.getMajor())
                .gpa(applicant.getGpa())
                .isContacted(savedApplicant.getIsContacted())
                .savedDate(savedApplicant.getSavedDate())
                .notes(savedApplicant.getNotes())
                .build();
    }

    private SavedApplicantPageResponse convertToPageResponse(Page<SavedApplicant> page) {
        List<SavedApplicantResponse> responses = page.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new SavedApplicantPageResponse(
                responses,
                page.getNumber(),
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }
}
