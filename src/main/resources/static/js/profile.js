(function () {
    'use strict';

    function getBasePath() {
        var raw = document.body && document.body.getAttribute('data-base-path');
        if (raw) {
            return raw;
        }
        return '/';
    }

    var basePath = getBasePath();

    function $(id) {
        return document.getElementById(id);
    }

    function openCreateProfileModal() {
        $('modalProfileTitle').innerText = 'Добавить профиль';
        $('profileForm').action = basePath + 'profiles/create';

        $('modalProfileId').value = '';
        $('modalProfileName').value = '';
        $('modalProfileLength').value = '16';

        // Имена обновлены в соответствии с DTO Record
        document.querySelector('input[name="isUppercase"]').checked = true;
        document.querySelector('input[name="isLowercase"]').checked = true;
        document.querySelector('input[name="isDigits"]').checked = true;
        document.querySelector('input[name="isSpecialChars"]').checked = true;
        document.querySelector('input[name="isDuplicateChars"]').checked = false;
        document.querySelector('input[name="isFavorite"]').checked = false;

        $('modalProfileCustomChars').value = '';
        $('modalProfileCipher').value = 'AES';
        $('modalProfileKdf').value = 'PBKDF_2';
        $('modalProfileIterations').value = '1000';
        $('')

        var fp = $('profileFormPage');
        if (fp) {
            fp.value = '0';
        }

        $('profileModal').classList.add('show');
    }

    function openEditProfileModal(btn) {
        $('modalProfileTitle').innerText = 'Редактировать профиль';
        var id = btn.getAttribute('data-id');
        $('profileForm').action = basePath + 'profiles/' + id + '/update';

        $('modalProfileId').value = id || '';
        $('modalProfileName').value = btn.getAttribute('data-name') || '';

        var length = btn.getAttribute('data-passwordLength');
        if (length) $('modalProfileLength').value = length;

        var custom = btn.getAttribute('data-customChars');
        $('modalProfileCustomChars').value = (custom && custom !== 'null') ? custom : '';

        var cipher = btn.getAttribute('data-cipher');
        if (cipher) $('modalProfileCipher').value = cipher;

        var kdf = btn.getAttribute('data-kdfAlgorithm');
        if (kdf) $('modalProfileKdf').value = kdf;

        var iterations = btn.getAttribute('data-iterations');
        $('modalProfileIterations').value = iterations ? iterations : '100000';

        // Имена обновлены в соответствии с DTO Record
        var upper = btn.getAttribute('data-uppercase');
        if (upper !== null) document.querySelector('input[name="isUppercase"]').checked = (upper === 'true');

        var lower = btn.getAttribute('data-lowercase');
        if (lower !== null) document.querySelector('input[name="isLowercase"]').checked = (lower === 'true');

        var digits = btn.getAttribute('data-digits');
        if (digits !== null) document.querySelector('input[name="isDigits"]').checked = (digits === 'true');

        var special = btn.getAttribute('data-specialChars');
        if (special !== null) document.querySelector('input[name="isSpecialChars"]').checked = (special === 'true');

        var avoid = btn.getAttribute('data-duplicateChars');
        if (avoid !== null) document.querySelector('input[name="isDuplicateChars"]').checked = (avoid === 'true');

        var fav = btn.getAttribute('data-favorite');
        if (fav !== null) document.querySelector('input[name="isFavorite"]').checked = (fav === 'true');

        $('profileModal').classList.add('show');
    }

    function closeProfileModal() {
        var modal = $('profileModal');
        if (modal) modal.classList.remove('show');
    }

    // Логика управления боковым меню (Sidebar) для мобильных устройств
    function openSidebar() {
        $('userSidebar').classList.add('open');
        $('sidebarOverlay').classList.add('show');
    }

    function closeSidebar() {
        $('userSidebar').classList.remove('open');
        $('sidebarOverlay').classList.remove('show');
    }

    function onDocumentClick(event) {
        // Открытие Sidebar
        var trigger = event.target.closest('.menu-trigger-btn');
        if (trigger) {
            openSidebar();
            return;
        }

        // Закрытие Sidebar по клику на фон
        if (event.target.id === 'sidebarOverlay') {
            closeSidebar();
            return;
        }

        // Закрытие Sidebar по клику на крестик
        var sidebarClose = event.target.closest('.close-sidebar-btn');
        if (sidebarClose && event.target.closest('#userSidebar')) {
            closeSidebar();
            return;
        }

        var createBtn = event.target.closest('.js-profile-open-create');
        if (createBtn) {
            openCreateProfileModal();
            return;
        }

        var editBtn = event.target.closest('.js-profile-open-edit');
        if (editBtn) {
            openEditProfileModal(editBtn);
            return;
        }

        var modalClose = event.target.closest('#profileModal .close-btn');
        if (modalClose) {
            closeProfileModal();
            return;
        }
    }

    function onModalClick(event) {
        if (event.target === $('profileModal')) {
            closeProfileModal();
        }
    }

    // Обработка удаления (всплывающее окно подтверждения)
    function onDeleteSubmit(event) {
        var form = event.target;
        if (!form.classList.contains('js-profile-delete-form')) {
            return;
        }
        var msg = form.getAttribute('data-confirm');
        if (msg && !window.confirm(msg)) {
            event.preventDefault();
        }
    }

    document.addEventListener('click', onDocumentClick);
    document.addEventListener('submit', onDeleteSubmit);

    var profileModal = $('profileModal');
    if (profileModal) {
        profileModal.addEventListener('click', onModalClick);
    }

})();