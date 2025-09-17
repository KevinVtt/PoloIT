## API REST BASICA.

### Utilice Visual Studio Code para este proyecto con variables de entorno.
#### Pasos a seguir.
- Crear la carpeta (si no esta creada) en la raiz del proyecto llamada .vscode.
- Dentro de la carpeta .vscode crear un archivo .json llamado launch.json
- Luego pegar estas variables de entorno

```{
    "configurations": [
        {
            "type": "java",
            "name": "Spring Boot-PoloItAceleradorApplication<polo-it-acelerador>",
            "request": "launch",
            "cwd": "${workspaceFolder}",
            "mainClass": "com.acelerador.polo_it_acelerador.PoloItAceleradorApplication",
            "projectName": "polo-it-acelerador",
            "args": "",
            "envFile": "${workspaceFolder}/.env",
            "env": {
                "DB_USER": "usuario_base_de_datos",
                "DB_PASSWORD": "password_base_de_datos"
            }
        }
    ]
}
