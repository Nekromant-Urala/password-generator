(() => {
    'use strict';

    const getBasePath = () => document.body?.getAttribute('data-base-path') || '/';
    const basePath = getBasePath();
    const $ = (id) => document.getElementById(id);

    const openCreateProfileModal = () => {
        $('modalProfileTitle').innerText = 'Добавить профиль';
        $('profileForm').action = `${basePath}profiles/create`;

        $('modalProfileId').value = '';
        $('modalProfileName').value = '';
        $('modalProfileLength').value = '16';

        document.querySelector('input[name="isUppercase"]').checked = true;
        document.querySelector('input[name="isLowercase"]').checked = true;
        document.querySelector('input[name="isDigits"]').checked = true;
        document.querySelector('input[name="isSpecialChars"]').checked = true;
        document.querySelector('input[name="isDuplicateChars"]').checked = false;
        document.querySelector('input[name="isFavorite"]').checked = false;

        $('modalProfileCustomChars').value = '';
        $('modalProfileCipher').value = 'AES';
        $('modalProfileKdf').value = 'PBKDF_2';

        const fp = $('profileFormPage');
        if (fp) fp.value = '0';

        $('profileModal').classList.add('show');
    };

    const openEditProfileModal = (btn) => {
        $('modalProfileTitle').innerText = 'Редактировать профиль';
        const id = btn.getAttribute('data-id');
        $('profileForm').action = `${basePath}profiles/${id}/update`;

        $('modalProfileId').value = id || '';
        $('modalProfileName').value = btn.getAttribute('data-name') || '';
        $('modalProfileLength').value = btn.getAttribute('data-passwordLength') || '16';

        const custom = btn.getAttribute('data-customChars');
        $('modalProfileCustomChars').value = (custom && custom !== 'null') ? custom : '';
        $('modalProfileCipher').value = 'AES';
        $('modalProfileKdf').value = 'PBKDF_2';

        document.querySelector('input[name="isUppercase"]').checked = btn.getAttribute('data-uppercase') === 'true';
        document.querySelector('input[name="isLowercase"]').checked = btn.getAttribute('data-lowercase') === 'true';
        document.querySelector('input[name="isDigits"]').checked = btn.getAttribute('data-digits') === 'true';
        document.querySelector('input[name="isSpecialChars"]').checked = btn.getAttribute('data-specialChars') === 'true';
        document.querySelector('input[name="isDuplicateChars"]').checked = btn.getAttribute('data-duplicateChars') === 'true';
        document.querySelector('input[name="isFavorite"]').checked = btn.getAttribute('data-favorite') === 'true';

        $('profileModal').classList.add('show');
    };

    const closeProfileModal = () => {
        $('profileModal').classList.remove('show');
    };

    document.addEventListener('click', (event) => {
        if (event.target.closest('.js-profile-open-create')) {
            openCreateProfileModal();
            return;
        }

        const editBtn = event.target.closest('.js-profile-open-edit');
        if (editBtn) {
            openEditProfileModal(editBtn);
            return;
        }

        if (event.target.closest('#profileModal .close-btn') || event.target === $('profileModal')) {
            closeProfileModal();
            return;
        }
    });

    document.addEventListener('submit', (event) => {
        const form = event.target;
        if (!form.classList.contains('js-profile-delete-form')) return;

        const msg = form.getAttribute('data-confirm');
        if (msg && !window.confirm(msg)) {
            event.preventDefault();
        }
    });
})();