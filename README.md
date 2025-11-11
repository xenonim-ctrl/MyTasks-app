<h1>🗒️ Task Manager (H2 + JavaFX)</h1>

<p>
Небольшое <strong>десктопное приложение</strong> для управления задачами с дедлайнами, приоритетами и статусами выполнения.  
Простой интерфейс, локальное хранение данных и современный тёмный стиль делают его удобным личным помощником.
</p>

<hr>

<h2>⚙️ Возможности</h2>

<ul>
  <li>✅ Добавление, редактирование и удаление задач</li>
  <li>🕒 Указание даты и времени дедлайна</li>
  <li>🔼 Установка приоритета (<code>Low</code> / <code>Medium</code> / <code>High</code>)</li>
  <li>☑️ Отметка выполненных задач</li>
  <li>💾 Сохранение данных во встроенную <strong>H2 Database</strong></li>
  <li>🌙 Современный тёмный интерфейс на <strong>JavaFX</strong></li>
  <li>📊 Отображение всех задач в удобном списке с интерактивным управлением</li>
</ul>

<hr>

<h2>🖼️ Интерфейс</h2>

<img src="https://github.com/user-attachments/assets/9cbdc30a-6023-438b-b97a-db097a113540" width="200" />
<img src="https://github.com/user-attachments/assets/8cbddf9f-aca3-4e83-9fe5-c2252013cec1" width="200" />!
<img src="https://github.com/user-attachments/assets/f4409d0e-4668-437a-a4a1-11de10c0d4b6" width="200" />!
<img src="https://github.com/user-attachments/assets/98910c49-4a1d-4d64-aaa0-24ee06bc61b3" width="200" />!
<img src="https://github.com/user-attachments/assets/5cad517f-a41a-4fa4-8197-12d48ed45a60" width="200" />!
<img src="https://github.com/user-attachments/assets/e4ac4344-7811-490d-b040-c447fe7a4cd2" width="200" />!

<hr>

<h2>🧩 Технологии</h2>

<ul>
  <li>☕ <strong>Java 17+</strong></li>
  <li>🎨 <strong>JavaFX</strong> — визуальный интерфейс</li>
  <li>💽 <strong>H2 Database</strong> — встроенное хранение данных</li>
  <li>🧰 <strong>Maven</strong> — управление зависимостями и сборкой</li>
</ul>

<hr>

<h2>🚀 Запуск проекта</h2>

<ol>
  <li>Клонировать репозиторий:
    <pre><code>git clone https://github.com/xenonim-ctrl/MyTasks-app.git</code></pre>
  </li>

  <li>Перейти в папку проекта:
    <pre><code>cd TaskManager</code></pre>
  </li>

  <li>Собрать и запустить через Maven:
    <pre><code>mvn clean javafx:run</code></pre>
  </li>
</ol>

<hr>

<h2>🗂️ Структура проекта</h2>

<pre>
<code>src/
 ├─ main/
 │   ├─ java/        → код приложения
 │   └─ resources/   → fxml, стили, конфиги
 └─ test/            → тесты
</code></pre>

<hr>

<h2>📌 Примечание</h2>

<p>
Приложение использует встроенную базу <strong>H2</strong>, которая автоматически создаётся и сохраняется локально —  
никакая установка или настройка внешней БД не требуется.
</p>

<p>
Task Manager — лёгкое и автономное решение для тех, кто ценит порядок и простоту.  
Отлично подойдёт как учебный проект по <strong>JavaFX + H2</strong> или как персональный планировщик.
</p>
