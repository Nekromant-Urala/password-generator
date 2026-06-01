(() => {
    'use strict';

    document.addEventListener('DOMContentLoaded', () => {
        const passwordForm = document.querySelector('form[action*="/settings/password"]');
        if (!passwordForm) return;

        passwordForm.addEventListener('submit', (event) => {
            const newPassword = document.getElementById('newPassword').value;
            const confirmPassword = document.getElementById('confirmPassword').value;

            // Валидация совпадения паролей до отправки формы на сервер
            if (newPassword !== confirmPassword) {
                event.preventDefault();
                alert('Ошибка: Новый пароль и подтверждение не совпадают!');
            }
        });
    });
})();