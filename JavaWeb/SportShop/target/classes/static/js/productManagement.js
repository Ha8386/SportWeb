// Sidebar functions
function toggleProductMenu(event) {
    console.log('Menu clicked!'); // Thêm dòng này để test
    event.preventDefault();
    const productMenu = document.querySelector('.product-menu');
    const submenu = document.getElementById('productSubmenu');

    productMenu.classList.toggle('active');
    submenu.classList.toggle('active');
}

// Close dropdown when clicking outside
document.addEventListener('click', function(event) {
    const productMenu = document.querySelector('.product-menu');
    const submenu = document.getElementById('productSubmenu');

    if (!productMenu.contains(event.target)) {
        productMenu.classList.remove('active');
        submenu.classList.remove('active');
    }
});


// Hàm format giá thành VND (thủ công)
function formatVND(price) {
    // Kiểm tra nếu giá trị không hợp lệ
    if (price === null || price === undefined || isNaN(price)) {
        return '0 VND';
    }

    // Chuyển đổi thành số và làm tròn
    const numPrice = Math.round(Number(price));

    // Format thủ công với dấu chấm phân cách
    const priceStr = numPrice.toString();
    const formatted = priceStr.replace(/\B(?=(\d{3})+(?!\d))/g, '.');

    return formatted + ' VND';
}

// Table
// JavaScript thêm tooltip cho các cell bị cắt
document.addEventListener('DOMContentLoaded', function() {
    // Thêm tooltip cho các cell có nội dung dài
    const tableCells = document.querySelectorAll('.table-container td');

    tableCells.forEach(function(cell) {
        // Bỏ qua cột thao tác (cột cuối)
        if (cell.cellIndex === cell.parentNode.cells.length - 1) {
            return;
        }

        // Kiểm tra nếu nội dung bị cắt
        if (cell.scrollWidth > cell.clientWidth) {
            cell.setAttribute('title', cell.textContent.trim());
        }
    });
});

function toggleProductView() {
    const select = document.getElementById('productViewSelect');
    const activeContainer = document.getElementById('activeProductsContainer');
    const deletedContainer = document.getElementById('deletedProductsContainer');

    if (select.value === 'active') {
        activeContainer.classList.remove('hidden');
        deletedContainer.classList.add('hidden');
    } else {
        activeContainer.classList.add('hidden');
        deletedContainer.classList.remove('hidden');
    }
}

document.addEventListener('DOMContentLoaded', function () {
    toggleProductView(); // Gọi lại để cập nhật hiển thị đúng sau khi load
});

// Modal functions
function openCreateModal() {
    document.getElementById('createProductModal').classList.add('active');
    document.body.style.overflow = 'hidden';
}

function closeCreateModal() {
    document.getElementById('createProductModal').classList.remove('active');
    document.body.style.overflow = 'auto';
    // Reset form
    document.getElementById('createProductForm').reset();
    // Clear image previews - FIX: Use correct ID
    const previewGrid = document.getElementById('modalImagePreviewGrid');
    if (previewGrid) {
        previewGrid.innerHTML = '';
    }
}

// Close modal when clicking outside
window.addEventListener('click', function(event) {
    const modal = document.getElementById('createProductModal');
    if (event.target === modal) {
        closeCreateModal();
    }
});

// Thêm vào event listener window click
    window.addEventListener('click', function(event) {
        const updateModal = document.getElementById('updateProductModal');
        if (event.target === updateModal) {
            closeUpdateModal();
        }
    });

// Close modal with Escape key
document.addEventListener('keydown', function(event) {
    if (event.key === 'Escape') {
        closeCreateModal();
    }
});

function restoreProduct(productId) {
    if (confirm("Bạn có chắc chắn muốn khôi phục sản phẩm này?")) {
            $.ajax({
                url: "/admin/product/restore/" + productId,
                type: "POST",
                success: function (result) {
                    showNotification("Khôi phục sản phẩm thành công!", "success");
                    setTimeout(() => {
                        window.location.reload(); // Reload trang để cập nhật danh sách
                    }, 1000);
                },
                error: function (xhr, status, error) {
                    let errorMessage = "Có lỗi xảy ra khi khôi phục sản phẩm!";
                    if (xhr.responseText) {
                        errorMessage = xhr.responseText;
                    }
                    showNotification(errorMessage, "error");
                }
            });
        }
}

function loadExistingImages(images) {
    const previewGrid = document.getElementById('updateImagePreviewGrid');
    previewGrid.innerHTML = '';

    if (images && images.length > 0) {
        images.forEach(image => {
            const previewItem = document.createElement("div");
            previewItem.className = "modal-preview-item";
            previewItem.innerHTML = `
                <img src="${image.url}" alt="Product Image"/>
                <button type="button" class="modal-remove-btn" onclick="removeExistingImage(this, ${image.id})">
                    <i class="fas fa-times"></i>
                </button>
                <input type="hidden" name="existingImageIds" value="${image.id}">
            `;
            previewGrid.appendChild(previewItem);
        });
    }
}

function previewUpdateImages(event) {
    const files = event.target.files;
    const previewGrid = document.getElementById("updateImagePreviewGrid");

    for (let file of files) {
        const reader = new FileReader();
        reader.onload = function (e) {
            const previewItem = document.createElement("div");
            previewItem.className = "modal-preview-item";
            previewItem.innerHTML = `
                <img src="${e.target.result}" alt="Preview"/>
                <button type="button" class="modal-remove-btn" onclick="removeUpdateImage(this)">
                    <i class="fas fa-times"></i>
                </button>
            `;
            previewGrid.appendChild(previewItem);
        };
        reader.readAsDataURL(file);
    }
}

function removeUpdateImage(button) {
    button.parentElement.remove();
}

function removeExistingImage(button, imageId) {
    button.parentElement.remove();
    // Add to delete list
    const deleteInput = document.createElement('input');
    deleteInput.type = 'hidden';
    deleteInput.name = 'deletedImageIds';
    deleteInput.value = imageId;
    document.getElementById('updateProductForm').appendChild(deleteInput);
}

// FIX: Rename function to match HTML call
function previewModalImages(event) {
    const previewGrid = document.getElementById("modalImagePreviewGrid");
    const files = event.target.files;

    if (!previewGrid) {
        console.error('Preview grid not found');
        return;
    }

    // Clear existing previews
    previewGrid.innerHTML = '';

    for (let file of files) {
        const reader = new FileReader();
        reader.onload = function (e) {
            const previewItem = document.createElement("div");
            previewItem.className = "modal-preview-item";
            previewItem.innerHTML = `
                <img src="${e.target.result}" alt="Preview"/>
                <button type="button" class="modal-remove-btn" onclick="removeModalImage(this)">
                    <i class="fas fa-times"></i>
                </button>
            `;
            previewGrid.appendChild(previewItem);
        };
        reader.readAsDataURL(file);
    }
}

// FIX: Add modal-specific remove function
function removeModalImage(button) {
    button.parentElement.remove();

    // Update file input if needed
    const fileInput = document.getElementById('modalFiles');
    if (fileInput && document.querySelectorAll('.modal-preview-item').length === 0) {
        fileInput.value = '';
    }
}

// Keep original functions for compatibility
function previewImages(event) {
    previewModalImages(event);
}

function removeImage(button) {
    removeModalImage(button);
}

function triggerFileInput() {
    document.getElementById('modalFiles').click();
}

// FIX: Simplified form submission - remove AJAX override
function submitCreateForm(event) {
    // Don't prevent default - let normal form submission work
    // event.preventDefault();

    // Just validate before submitting
    if (!validateForm()) {
        event.preventDefault();
        return false;
    }

    // Show loading state
    const submitBtn = event.target.querySelector('button[type="submit"]');
    if (submitBtn) {
        const originalText = submitBtn.innerHTML;
        submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Đang tạo...';
        submitBtn.disabled = true;

        // Re-enable after 10 seconds in case of issues
        setTimeout(() => {
            submitBtn.innerHTML = originalText;
            submitBtn.disabled = false;
        }, 10000);
    }

    return true;
}

// Excel upload functionality
function submitExcelForm(event) {
    const fileInput = document.getElementById('excelFile');
    const file = fileInput.files[0];

    if (!file) {
        showNotification('Vui lòng chọn file Excel!', 'error');
        event.preventDefault();
        return false;
    }

    // Validate file type
    const allowedTypes = [
        'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
        'application/vnd.ms-excel'
    ];

    if (!allowedTypes.includes(file.type)) {
        showNotification('Chỉ cho phép file Excel (.xlsx, .xls)!', 'error');
        event.preventDefault();
        return false;
    }

    // Show loading
    showNotification('Đang xử lý file Excel...', 'info');
    return true;
}

// Notification system
function showNotification(message, type = 'info') {
    // Remove existing notifications
    const existingNotifications = document.querySelectorAll('.notification');
    existingNotifications.forEach(notification => notification.remove());

    // Create notification element
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: white;
        padding: 15px 20px;
        border-radius: 8px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.1);
        z-index: 9999;
        transform: translateX(400px);
        transition: transform 0.3s ease;
        border-left: 4px solid ${getNotificationColor(type)};
    `;

    notification.innerHTML = `
        <div class="notification-content" style="display: flex; align-items: center; gap: 10px;">
            <i class="fas ${getNotificationIcon(type)}" style="color: ${getNotificationColor(type)};"></i>
            <span>${message}</span>
            <button class="notification-close" onclick="this.parentElement.parentElement.remove()"
                    style="background: none; border: none; cursor: pointer; margin-left: auto;">
                <i class="fas fa-times"></i>
            </button>
        </div>
    `;

    // Add to page
    document.body.appendChild(notification);

    // Auto remove after 5 seconds
    setTimeout(() => {
        if (notification.parentElement) {
            notification.remove();
        }
    }, 5000);

    // Animate in
    setTimeout(() => {
        notification.style.transform = 'translateX(0)';
    }, 100);
}

function getNotificationIcon(type) {
    switch(type) {
        case 'success': return 'fa-check-circle';
        case 'error': return 'fa-exclamation-circle';
        case 'warning': return 'fa-exclamation-triangle';
        default: return 'fa-info-circle';
    }
}

function getNotificationColor(type) {
    switch(type) {
        case 'success': return '#28a745';
        case 'error': return '#dc3545';
        case 'warning': return '#ffc107';
        default: return '#17a2b8';
    }
}

// Delete product function
function softDeleteProduct(productId) {
    if (confirm("Bạn có chắc chắn muốn xóa sản phẩm này?")) {
        $.ajax({
            url: "/admin/product/delete/" + productId,
            type: "PUT",
            success: function (result) {
                showNotification("Xóa sản phẩm thành công!", "success");
                setTimeout(() => {
                    window.location.reload(); // Reload trang để cập nhật danh sách
                }, 1000);
            },
            error: function (xhr, status, error) {
                let errorMessage = "Có lỗi xảy ra khi xóa sản phẩm!";
                if (xhr.responseText) {
                    errorMessage = xhr.responseText;
                }
                showNotification(errorMessage, "error");
            }
        });
    }
}

// Delete product function
function deleteProduct(productId) {
    if (confirm("Bạn có chắc chắn muốn xóa vĩnh viễn sản phẩm này?")) {
        $.ajax({
            url: "/admin/product/delete/" + productId,
            type: "DELETE",
            success: function (result) {
                showNotification("Xóa sản phẩm thành công!", "success");
                setTimeout(() => {
                    window.location.reload(); // Reload trang để cập nhật danh sách
                }, 1000);
            },
            error: function (xhr, status, error) {
                let errorMessage = "Có lỗi xảy ra khi xóa sản phẩm!";
                if (xhr.responseText) {
                    errorMessage = xhr.responseText;
                }
                showNotification(errorMessage, "error");
            }
        });
    }
}

// Sorting functionality
let urlParams = new URLSearchParams(window.location.search);
let sortField = urlParams.get('sortField') || 'id';
let sortDir = urlParams.get('sortDir') || 'asc';

function sortTable(field) {
    // If clicking the same column, toggle direction
    if (field === sortField) {
        sortDir = sortDir === 'asc' ? 'desc' : 'asc';
    } else {
        sortField = field;
        sortDir = 'asc';
    }

    // Keep current page and size
    const page = urlParams.get('page') || 0;
    const size = urlParams.get('size') || 10;
    const name = urlParams.get('name') || '';

    // Redirect with new sorting parameters
    const params = new URLSearchParams({
        page: page,
        size: size,
        sortField: sortField,
        sortDir: sortDir
    });

    if (name) {
        params.append('name', name);
    }

    window.location.href = `/admin/product?${params.toString()}`;
}

function sortDeletedTable(field) {
    // If clicking the same column, toggle direction
    if (field === sortField) {
        sortDir = sortDir === 'asc' ? 'desc' : 'asc';
    } else {
        sortField = field;
        sortDir = 'asc';
    }

    // Keep current page and size
    const page = urlParams.get('page') || 0;
    const size = urlParams.get('size') || 10;
    const name = urlParams.get('name') || '';

    // Redirect with new sorting parameters
    const params = new URLSearchParams({
        page: page,
        size: size,
        sortField: sortField,
        sortDir: sortDir
    });

    if (name) {
        params.append('name', name);
    }

    window.location.href = `/admin/product?${params.toString()}`;
}

// Form validation
function validateForm() {
    const form = document.getElementById('createProductForm');
    const requiredFields = form.querySelectorAll('[required]');
    let isValid = true;
    let errorMessages = [];

    requiredFields.forEach(field => {
        if (!field.value.trim()) {
            field.style.borderColor = '#dc3545';
            isValid = false;
            const label = field.previousElementSibling;
            if (label) {
                errorMessages.push(`${label.textContent} là bắt buộc`);
            }
        } else {
            field.style.borderColor = '#e0e0e0';
        }
    });

    // Validate images
    const fileInput = document.getElementById('modalFiles');
    if (!fileInput || !fileInput.files.length) {
        showNotification('Vui lòng chọn ít nhất một ảnh sản phẩm!', 'error');
        isValid = false;
    }

    // Validate price and discount
    const priceField = form.querySelector('[name="price"]');
    const discountField = form.querySelector('[name="discount"]');

    if (priceField) {
        const price = parseFloat(priceField.value);
        if (isNaN(price) || price < 0) {
            showNotification('Giá sản phẩm phải là số dương!', 'error');
            priceField.style.borderColor = '#dc3545';
            isValid = false;
        }
    }

    if (discountField) {
        const discount = parseFloat(discountField.value);
        if (isNaN(discount) || discount < 0 || discount > 100) {
            showNotification('Giảm giá phải từ 0 đến 100%!', 'error');
            discountField.style.borderColor = '#dc3545';
            isValid = false;
        }
    }

    // Show first error message
    if (errorMessages.length > 0) {
        showNotification(errorMessages[0], 'error');
    }

    return isValid;
}

// Hàm sao chép sản phẩm
function copyProduct(productId) {
    // Tìm row chứa sản phẩm theo ID
    const productRow = Array.from(document.querySelectorAll('#activeProductsContainer tbody tr')).find(row => {
        const firstCell = row.querySelector('td:first-child');
        return firstCell && firstCell.textContent.trim() === productId.toString();
    });

    if (!productRow) {
        showNotification('Không tìm thấy sản phẩm!', 'error');
        return;
    }

    // Lấy thông tin từ các cell trong row
    const cells = productRow.querySelectorAll('td');

    if (cells.length < 9) {
        showNotification('Dữ liệu sản phẩm không đầy đủ!', 'error');
        return;
    }

    // Lấy dữ liệu từ các cell (theo thứ tự trong bảng)
    const productData = {
        id: cells[0].textContent.trim(),
        brandName: cells[1].textContent.trim(),
        categoryName: cells[2].textContent.trim(),
        name: cells[3].textContent.trim(),
        color: cells[4].textContent.trim(),
        price: cells[5].textContent.trim(),
        discount: cells[6].textContent.trim(),
        quantity: cells[7].textContent.trim(),
        description: cells[8].textContent.trim()
    };

    // Mở modal tạo sản phẩm
    openCreateModal();

    // Đợi modal hiển thị hoàn toàn
    setTimeout(() => {
        fillProductForm(productData);
    }, 200);
}

// Hàm điền thông tin vào form
function fillProductForm(productData) {
    try {
        // Điền các trường input text
        const nameInput = document.querySelector('#createProductForm input[name="name"]');
        if (nameInput) nameInput.value = productData.name + ' (Copy)';

        const colorInput = document.querySelector('#createProductForm input[name="color"]');
        if (colorInput) colorInput.value = productData.color;

        const priceInput = document.querySelector('#createProductForm input[name="price"]');
        if (priceInput) priceInput.value = productData.price;

        const discountInput = document.querySelector('#createProductForm input[name="discount"]');
        if (discountInput) discountInput.value = productData.discount;

        const quantityInput = document.querySelector('#createProductForm input[name="quantity"]');
        if (quantityInput) quantityInput.value = productData.quantity;

        const descriptionInput = document.querySelector('#createProductForm textarea[name="description"]');
        if (descriptionInput) descriptionInput.value = productData.description;

        // Tìm và chọn thương hiệu
        const brandSelect = document.querySelector('#createProductForm select[name="brandId"]');
        if (brandSelect) {
            const brandOptions = brandSelect.querySelectorAll('option');
            for (let option of brandOptions) {
                if (option.textContent.trim() === productData.brandName) {
                    option.selected = true;
                    brandSelect.value = option.value;
                    break;
                }
            }
        }

        // Tìm và chọn danh mục
        const categorySelect = document.querySelector('#createProductForm select[name="categoryId"]');
        if (categorySelect) {
            const categoryOptions = categorySelect.querySelectorAll('option');
            for (let option of categoryOptions) {
                if (option.textContent.trim() === productData.categoryName) {
                    option.selected = true;
                    categorySelect.value = option.value;
                    break;
                }
            }
        }

        // Hiển thị thông báo thành công
        showNotification('Đã sao chép thông tin sản phẩm thành công!', 'success');

    } catch (error) {
        console.error('Lỗi khi điền form:', error);
        showNotification('Có lỗi xảy ra khi sao chép thông tin sản phẩm!', 'error');
    }
}

// Initialize page
window.onload = function() {
    console.log('Page loaded, initializing...');

    // Set sort icons
    const iconMap = {
        'asc': '↑',
        'desc': '↓'
    };

    const sortIconId = `sort-icon-${sortField}`;
    const iconElement = document.getElementById(sortIconId);
    if (iconElement) {
        iconElement.innerText = iconMap[sortDir];
    }

    // Add event listeners - FIX: Remove AJAX override
    const createForm = document.getElementById('createProductForm');
    if (createForm) {
        console.log('Found create form, adding validation');
        // Just add validation, don't override submission
        createForm.addEventListener('submit', function(event) {
            console.log('Form submit triggered');
            return submitCreateForm(event);
        });
    }

    const excelForm = document.querySelector('form[action*="upload"]');
    if (excelForm) {
        excelForm.addEventListener('submit', submitExcelForm);
    }

    // Add keyboard shortcuts
    document.addEventListener('keydown', function(event) {
        // Ctrl + N to open create modal
        if (event.ctrlKey && event.key === 'n') {
            event.preventDefault();
            openCreateModal();
        }
    });

    console.log('Initialization complete');

    // Thêm vào cuối hàm window.onload
    const updateForm = document.getElementById('updateProductForm');
    if (updateForm) {
        updateForm.addEventListener('submit', function(event) {
            event.preventDefault();

            const formData = new FormData(updateForm);
            const productId = document.getElementById('updateProductId').value;

            $.ajax({
                url: `/admin/product/update/${productId}`,
                type: 'POST',
                data: formData,
                processData: false,
                contentType: false,
                success: function(response) {
                    closeUpdateModal();
                    showNotification('Cập nhật sản phẩm thành công!', 'success');
                    setTimeout(() => {
                        window.location.reload();
                    }, 1000);
                },
                error: function(xhr) {
                    let errorMessage = "Có lỗi xảy ra khi cập nhật sản phẩm!";
                    if (xhr.responseText) {
                        errorMessage = xhr.responseText;
                    }
                    showNotification(errorMessage, 'error');
                }
            });
        });
    }
};

function initMergedTable() {
    // Tạo container cho filter controls
    const activeContainer = document.getElementById('activeProductsContainer');
    const deletedContainer = document.getElementById('deletedProductsContainer');

    // Tạo filter controls
    const filterHTML = `
        <div class="filter-controls">
            <label for="statusFilter">Lọc theo trạng thái:</label>
            <select id="statusFilter" onchange="filterByStatus()">
                <option value="all">Tất cả</option>
                <option value="active" selected>Đang hoạt động</option>
                <option value="deleted">Đã xóa</option>
            </select>
        </div>
    `;

    // Thêm filter vào trước bảng active
    activeContainer.insertAdjacentHTML('beforebegin', filterHTML);

    // Gộp dữ liệu từ 2 bảng
    mergeTableData();

    // Ẩn bảng deleted ban đầu
    deletedContainer.style.display = 'none';
}

// 3. Function để gộp dữ liệu (paste vào file JS)
function mergeTableData() {
    const activeTable = document.querySelector('#activeProductsContainer tbody');
    const deletedTable = document.querySelector('#deletedProductsContainer tbody');

    // Lấy tất cả rows từ bảng deleted
    const deletedRows = Array.from(deletedTable.querySelectorAll('tr'));

    // Xử lý từng row deleted để thêm status và action buttons
    deletedRows.forEach(row => {
        const cells = row.querySelectorAll('td');
        if (cells.length > 0) {
            // Thêm status badge vào cột đầu tiên
            const firstCell = cells[0];
            firstCell.innerHTML = `
                ${firstCell.textContent}
                <span class="status-badge status-deleted">Đã xóa</span>
            `;

            // Cập nhật action buttons (giữ nguyên restore và delete buttons)
            const actionCell = cells[cells.length - 1];
            // Action cell đã có sẵn restore và delete buttons từ HTML gốc

            // Thêm class để ẩn/hiện row
            row.classList.add('deleted-row');
            row.style.display = 'none'; // Ẩn ban đầu
        }
    });

    // Thêm status badge cho active rows
    const activeRows = Array.from(activeTable.querySelectorAll('tr'));
    activeRows.forEach(row => {
        const cells = row.querySelectorAll('td');
        if (cells.length > 0) {
            const firstCell = cells[0];
            firstCell.innerHTML = `
                ${firstCell.textContent}
                <span class="status-badge status-active">Hoạt động</span>
            `;
            row.classList.add('active-row');
        }
    });

    // Di chuyển deleted rows vào active table
    deletedRows.forEach(row => {
        activeTable.appendChild(row);
    });
}

// 4. Function để lọc theo status (paste vào file JS)
function filterByStatus() {
    const filter = document.getElementById('statusFilter').value;
    const activeRows = document.querySelectorAll('.active-row');
    const deletedRows = document.querySelectorAll('.deleted-row');

    // Ẩn/hiện pagination của bảng deleted
    const deletedPagination = document.querySelector('#deletedProductsContainer .pagination');
    const activePagination = document.querySelector('#activeProductsContainer .pagination');

    switch(filter) {
        case 'all':
            activeRows.forEach(row => row.style.display = '');
            deletedRows.forEach(row => row.style.display = '');
            activePagination.style.display = 'block';
            if (deletedPagination) deletedPagination.style.display = 'none';
            break;

        case 'active':
            activeRows.forEach(row => row.style.display = '');
            deletedRows.forEach(row => row.style.display = 'none');
            activePagination.style.display = 'block';
            if (deletedPagination) deletedPagination.style.display = 'none';
            break;

        case 'deleted':
            activeRows.forEach(row => row.style.display = 'none');
            deletedRows.forEach(row => row.style.display = '');
            activePagination.style.display = 'none';
            if (deletedPagination) deletedPagination.style.display = 'block';
            break;
    }
}

// 5. Khởi tạo khi DOM loaded (paste vào cuối file JS)
document.addEventListener('DOMContentLoaded', function() {
    // Thêm CSS
    const style = document.createElement('style');
    style.textContent = statusCSS;
    document.head.appendChild(style);

    // Khởi tạo merged table
    initMergedTable();
});



// Auto-save draft functionality (optional) - Remove localStorage usage
function saveDraft() {
    // Commented out localStorage usage
    console.log('Draft save disabled in this environment');
}

function loadDraft() {
    // Commented out localStorage usage
    console.log('Draft load disabled in this environment');
}

function clearDraft() {
    // Commented out localStorage usage
    console.log('Draft clear disabled in this environment');
}