package com.abcbank.images.services;

import com.abcbank.images.domain.dto.item.ItemResponse;
import com.abcbank.images.domain.dto.upload.UploadResponse;
import com.abcbank.images.exceptions.FileProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UploadService {

    private final FileStorageService fileStorageService;
    private final ItemService itemService;

    /**
     * Uploads an image and creates the corresponding workflow item.
     *
     * The physical image is stored first.
     * The resulting image URL is then stored on the workflow item,
     * and the item is immediately advanced by the "UPLOAD" workflow
     * action (see ItemService.create), so the response reflects
     * wherever that lands the item — not the initial queue.
     */
    @Transactional
    public UploadResponse uploadImage(
            MultipartFile file,
            String description,
            String idNumber,
            String customerName,
            String phoneNumber,
            String keycloakUserId,
            boolean adminRole
    ) {

        String storedFilename = null;

        try {

            // -----------------------------------------------------
            // 1. Store physical image
            // -----------------------------------------------------

            storedFilename =
                    fileStorageService.storeImage(file);

            // -----------------------------------------------------
            // 2. Build URL used by frontend
            // -----------------------------------------------------

            String imageUrl =
                    "/api/v1/uploads/files/"
                            + storedFilename;

            // -----------------------------------------------------
            // 3. Create workflow item (this also runs the UPLOAD
            //    action, moving the item to its next queue)
            // -----------------------------------------------------

            ItemResponse item =
                    itemService.create(
                            com.abcbank.images.domain.dto.item.ItemRequest
                                    .builder()
                                    .imageUrl(imageUrl)
                                    .description(description)
                                    .idNumber(idNumber)
                                    .customerName(customerName)
                                    .phoneNumber(phoneNumber)
                                    .build(),
                            keycloakUserId,
                            adminRole
                    );

            // -----------------------------------------------------
            // 4. Return upload information
            // -----------------------------------------------------

            return UploadResponse.builder()
                    .itemId(item.getId())
                    .fileName(file.getOriginalFilename())
                    .contentType(file.getContentType())
                    .fileSize(file.getSize())
                    .imageUrl(imageUrl)
                    .description(item.getDescription())
                    .idNumber(item.getIdNumber())
                    .customerName(item.getCustomerName())
                    .phoneNumber(item.getPhoneNumber())
                    .status(
                            item.getStatus() != null
                                    ? item.getStatus().name()
                                    : null
                    )
                    .currentQueue(
                            item.getCurrentQueueName()
                    )
                    .build();

        } catch (RuntimeException ex) {

            // -----------------------------------------------------
            // Roll back physical file if DB creation or the UPLOAD
            // transition fails, but let the original exception
            // (e.g. "no transition configured for UPLOAD") through
            // unwrapped so its real message/status reaches the
            // caller instead of a generic 500.
            // -----------------------------------------------------

            if (storedFilename != null) {

                try {
                    fileStorageService.deleteImage(
                            storedFilename
                    );
                } catch (Exception ignored) {
                    // Original exception is more important.
                }
            }

            throw ex;

        } catch (Exception ex) {

            if (storedFilename != null) {

                try {
                    fileStorageService.deleteImage(
                            storedFilename
                    );
                } catch (Exception ignored) {
                    // Original exception is more important.
                }
            }

            throw new FileProcessingException(
                    "Failed to upload image and create workflow item",
                    ex
            );
        }
    }
}