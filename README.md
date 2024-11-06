# MoodNotes
Описание:
MoodNotes — это мобильное приложение на основе Jetpack Compose для создания заметок о настроении. Оно позволяет пользователям сохранять, редактировать и удалять записи с описанием настроения и заметками. Приложение поддерживает навигацию и управление данными через локальную базу данных с использованием Room и ViewModel.

Основные функции:
Добавление новой заметки: Пользователь может создать заметку, указав свое настроение и сопроводительный текст.
Редактирование существующих заметок: Поддерживается обновление ранее созданных записей.
Просмотр истории заметок: Заметки отображаются в обратном хронологическом порядке.
Удаление отдельных записей: Возможность удаления отдельных заметок.
Удаление всех заметок: Кнопка для удаления всей истории заметок.
Технические детали
Архитектура: Использование MVVM (Model-View-ViewModel) для четкой организации кода и разделения ответственности.
Room Database: Для хранения данных используется локальная база данных Room.
Jetpack Compose: Для создания UI интерфейса применяется современная библиотека UI-фреймворка.
Корутины и Flow: Обработка асинхронных операций и потоков данных.
Навигация: Реализована при помощи NavHost и composable маршрутов.
