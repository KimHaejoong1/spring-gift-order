function openUpdateModal(id, email, role) {
    document.getElementById('update-id').value = id;
    document.getElementById('update-email').value = email;
    document.getElementById('update-role').value = role;
    document.getElementById('updateModal').style.display = 'block';
    document.getElementById('updateErrorMessage').style.display = 'none';
}

function closeModal() {
    document.querySelectorAll('.modal').forEach(modal => {
        modal.style.display = 'none';
    });
    document.getElementById('createErrorMessage').style.display = 'none';
    document.getElementById('updateErrorMessage').style.display = 'none';
}

document.addEventListener('DOMContentLoaded', function() {
    const body = document.body;
    const showCreateModal = body.dataset.showCreateModal === 'true';
    const showUpdateModal = body.dataset.showUpdateModal === 'true';

    if (showCreateModal) {
        document.getElementById('createModal').style.display = 'block';
    }
    if (showUpdateModal) {
        document.getElementById('updateModal').style.display = 'block';
    }
});

document.querySelector('.create-btn').addEventListener('click', function () {
    document.getElementById('createModal').style.display = 'block';
});

document.querySelectorAll('.update-btn').forEach(btn => {
    btn.addEventListener('click', function () {
        const id = this.dataset.id;
        const email = this.dataset.email;
        const role = this.dataset.role;
        openUpdateModal(id,email,role);
    });
});

document.querySelectorAll('.delete-btn').forEach(btn => {
    btn.addEventListener('click', function () {
        document.getElementById('delete-id').value = this.dataset.id;
        document.getElementById('deleteModal').style.display = 'block';
    });
});

document.querySelectorAll('.cancel-btn').forEach(btn => {
    btn.addEventListener('click', function () {
        closeModal();
    });
});
