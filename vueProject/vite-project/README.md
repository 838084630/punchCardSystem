#勤怠システム初期化手順(ローカル)
### description
Vue 3 + TypeScript + Vite
## Frontend
#### 1.環境準備
- node.js 14.18.1
- npm 6.14.15
- java 1.8  
  
#### 2.QuickStart  
Vscodeをインストール、下記のコマンドを実行
- `cd vite-project`
- `npm install`
- `npm run dev`

## Backend
#### 1.環境準備
- java 1.8 
- postgresql  
  ①インストール参照：（MacM1)https://blog.csdn.net/HelloYoungr/article/details/127988734  
  ②create db (name:pcs) and schema (name:attendance)  
  ③*.sql&DDLの導入(PATH:punchCardSystem/javaProject/src/main/resources/come/example/demo/sql)
  
#### 2.QuickStart  
IntelliJ IDEAをインストールする
- Run demoApplication
   
最後は起動した画面からログインする(user:admin pwd:123456)