package com.abcbank.images.services;

import com.abcbank.images.domain.dto.item.ItemActionRequest;
import com.abcbank.images.domain.dto.item.ItemRequest;
import com.abcbank.images.domain.dto.item.ItemResponse;
import com.abcbank.images.domain.dto.transition.ItemTransitionResponse;
import com.abcbank.images.domain.entities.Department;
import com.abcbank.images.domain.entities.Item;
import com.abcbank.images.domain.entities.Queue;
import com.abcbank.images.domain.entities.User;
import com.abcbank.images.domain.enums.ItemStatus;
import com.abcbank.images.exceptions.ResourceNotFoundException;
import com.abcbank.images.exceptions.UnauthorizedActionException;
import com.abcbank.images.mappers.ItemMapper;
import com.abcbank.images.repositories.ItemRepository;
import com.abcbank.images.repositories.QueueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

private final ItemRepository itemRepository;
private final QueueRepository queueRepository;
private final UserService userService;
private final WorkflowService workflowService;
private final TransitionService transitionService;
private final ItemMapper itemMapper;
private final FileStorageService fileStorageService;

// ============================================================
// CREATE ITEM
// ============================================================

@Transactional
public ItemResponse create(
        ItemRequest request,
        String keycloakUserId,
        boolean adminRole
) {

    User user =
            userService.getByKeycloakId(keycloakUserId);

    /*
     * Every item is rooted in the department (branch) of the
     * Teller who created it. This is what keeps one branch's
     * items out of another branch's queues later on.
     */

    Department department =
            user.getDepartment();

    if (department == null) {

        throw new UnauthorizedActionException(
                "User is not assigned to a department; cannot start a workflow item"
        );
    }

    /*
     * Dynamically resolve this department's initial queue.
     *
     * Each department configures its own initial queue
     * (Queue.initial = true, scoped to that department) —
     * there is no longer a single global initial queue.
     */

    Queue initialQueue =
            queueRepository
                    .findByInitialTrueAndDepartment(department)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "No initial workflow queue has been configured for department: "
                                            + department.getName()
                            )
                    );

    /*
     * Create the workflow item.
     *
     * Every new item enters the configured initial queue for
     * its department first — this is required so the UPLOAD
     * action below has a queue/department pair to resolve a
     * transition against (queues own their configured actions).
     */

    Item item =
            Item.builder()
                    .description(
                            request.getDescription()
                    )
                    .imageUrl(
                            request.getImageUrl()
                    )
                    .idNumber(
                            request.getIdNumber()
                    )
                    .customerName(
                            request.getCustomerName()
                    )
                    .phoneNumber(
                            request.getPhoneNumber()
                    )
                    .currentQueue(
                            initialQueue
                    )
                    .status(
                            ItemStatus.ACTIVE
                    )
                    .createdBy(
                            user
                    )
                    .department(
                            department
                    )
                    .build();

    Item savedItem =
            itemRepository.save(item);

    /*
     * Immediately run the "UPLOAD" workflow action so the item
     * proceeds to whatever queue is configured as UPLOAD's
     * destination for this department, exactly like any other
     * workflow action. Starting a queue is the trigger for this
     * action — it isn't something a Teller separately selects
     * afterwards (that's why "Upload" never appears as a
     * selectable action on an existing item).
     */

    Item transitionedItem =
            workflowService.executeAction(
                    savedItem.getId(),
                    "UPLOAD",
                    keycloakUserId,
                    null,
                    adminRole
            );

    return itemMapper.toResponse(
            transitionedItem
    );
}

// ============================================================
// GET ENTITY
// ============================================================

@Transactional(readOnly = true)
public Item getEntityById(
        Long id
) {

    return itemRepository
            .findById(id)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Item not found: " + id
                    )
            );
}

// ============================================================
// GET ITEM
// ============================================================

@Transactional(readOnly = true)
public ItemResponse getById(
        Long id
) {

    return itemMapper.toResponse(
            getEntityById(id)
    );
}

// ============================================================
// GET ALL ITEMS
// ============================================================

@Transactional(readOnly = true)
public List<ItemResponse> getAll() {

    return itemRepository
            .findAll()
            .stream()
            .map(
                    itemMapper::toResponse
            )
            .toList();
}

// ============================================================
// GET ACTIVE ITEMS
// ============================================================

@Transactional(readOnly = true)
public List<ItemResponse> getActiveItems() {

    return itemRepository
            .findByStatus(
                    ItemStatus.ACTIVE
            )
            .stream()
            .map(
                    itemMapper::toResponse
            )
            .toList();
}

// ============================================================
// GET ITEMS BY QUEUE
// ============================================================

@Transactional(readOnly = true)
public List<ItemResponse> getByQueue(
        Long queueId
) {

    Queue queue =
            queueRepository
                    .findById(queueId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Queue not found: "
                                            + queueId
                            )
                    );

    return itemRepository
            .findByCurrentQueue(queue)
            .stream()
            .map(
                    itemMapper::toResponse
            )
            .toList();
}

// ============================================================
// GET ACTIVE ITEMS BY QUEUE
// ============================================================

@Transactional(readOnly = true)
public List<ItemResponse> getActiveByQueue(
        Long queueId
) {

    Queue queue =
            queueRepository
                    .findById(queueId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Queue not found: "
                                            + queueId
                            )
                    );

    return itemRepository
            .findByCurrentQueueAndStatus(
                    queue,
                    ItemStatus.ACTIVE
            )
            .stream()
            .map(
                    itemMapper::toResponse
            )
            .toList();
}

// ============================================================
// GET ITEMS BY STATUS
// ============================================================

@Transactional(readOnly = true)
public List<ItemResponse> getByStatus(
        ItemStatus status
) {

    return itemRepository
            .findByStatus(status)
            .stream()
            .map(
                    itemMapper::toResponse
            )
            .toList();
}

// ============================================================
// GET ITEMS CREATED BY USER
// ============================================================

@Transactional(readOnly = true)
public List<ItemResponse> getCreatedByUser(
        Long userId
) {

    return itemRepository
            .findByCreatedById(userId)
            .stream()
            .map(
                    itemMapper::toResponse
            )
            .toList();
}

// ============================================================
// EXECUTE WORKFLOW ACTION
// ============================================================

@Transactional
public ItemResponse executeAction(
        Long itemId,
        ItemActionRequest request,
        String keycloakUserId,
        boolean adminRole
) {

    Item item =
            workflowService.executeAction(
                    itemId,
                    request.getAction(),
                    keycloakUserId,
                                        request.getComment(),
                                        adminRole
            );

    return itemMapper.toResponse(
            item
    );
}

// ============================================================
// ITEM HISTORY
// ============================================================

@Transactional(readOnly = true)
public List<ItemTransitionResponse> getHistory(
        Long itemId
) {

    return transitionService
            .getItemHistory(itemId);
}

// ============================================================
// AMEND ITEM (optionally replacing the photo)
// ============================================================

/**
 * Amends an item: optionally replaces its photo and/or its
 * customer identity fields, then executes the "AMEND" workflow
 * action so the item moves on exactly like any other action.
 *
 * The image swap happens first and is persisted before the
 * workflow transition runs, so the transition's own item lookup
 * sees the new photo/details. If the workflow step then fails
 * (wrong queue, no transition configured, etc.), the newly
 * uploaded file is cleaned up and the old photo is left in place.
 */
@Transactional
public ItemResponse amendWithImage(
        Long itemId,
        MultipartFile file,
        String idNumber,
        String customerName,
        String phoneNumber,
        String description,
        String comment,
        String keycloakUserId,
        boolean adminRole
) {

    Item item = getEntityById(itemId);

    String previousImageUrl = item.getImageUrl();
    String newlyStoredFilename = null;

    if (file != null && !file.isEmpty()) {

        newlyStoredFilename =
                fileStorageService.storeImage(file);

        item.setImageUrl(
                "/api/v1/uploads/files/" + newlyStoredFilename
        );
    }

    if (idNumber != null && !idNumber.isBlank()) {
        item.setIdNumber(idNumber);
    }

    if (customerName != null && !customerName.isBlank()) {
        item.setCustomerName(customerName);
    }

    if (phoneNumber != null && !phoneNumber.isBlank()) {
        item.setPhoneNumber(phoneNumber);
    }

    if (description != null) {
        item.setDescription(description);
    }

    try {

        itemRepository.save(item);

        Item updated =
                workflowService.executeAction(
                        itemId,
                        "AMEND",
                        keycloakUserId,
                        comment,
                        adminRole
                );

        if (newlyStoredFilename != null
                && previousImageUrl != null) {
            deleteStoredImageQuietly(previousImageUrl);
        }

        return itemMapper.toResponse(updated);

    } catch (RuntimeException ex) {

        if (newlyStoredFilename != null) {
            fileStorageService.deleteImage(newlyStoredFilename);
        }

        throw ex;
    }
}

private void deleteStoredImageQuietly(String imageUrl) {

    String prefix = "/api/v1/uploads/files/";

    if (imageUrl == null || !imageUrl.startsWith(prefix)) {
        return;
    }

    try {
        fileStorageService.deleteImage(
                imageUrl.substring(prefix.length())
        );
    } catch (Exception ignored) {
        // Best-effort cleanup — the transition already
        // succeeded and is more important than an orphaned file.
    }
}

}