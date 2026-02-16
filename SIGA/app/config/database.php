<?php
class Database {
    public static function connect() {
        return new mysqli("localhost", "root", "", "siga_escom");
    }
}
