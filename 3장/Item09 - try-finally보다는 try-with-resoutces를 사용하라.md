### 1. try-finally 의 경우
- close 메서드로 직접 닫아줘야 하는 자원 (InputStream, OutputStream, java.sql.Connection 등)
- 전통적으로 쓰이는 자원닫는 방식

```java
static String firstLineOfFile(String path) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(peth));
    try {
        return br.readLine();
    } finally{
        br.close();
    }
}
```

자원이 둘 이상인 경우
```java
static void copy(String scr, String dst) throws IOException {
    InputStream in = new FileInputStream(src);
    try {
        OutputStream out = new FileInputStream(dst);
        try{
            byte[] buf = new byte[BUFFER_SIZE];
            int n;
            while((n = in.read(buf)) >= 0)
                out.write(buf, 0, n);
        } finally {
            out.close();
        }
    } finally {
        in.close();
    }
}
```


### 2. try-with-resources를 적용해보자.
- java7 부터 명시적으로 close를 호출하지 않아도 AuthCloseable이 자동으로 호출해줍니다.
- 자원을 쉽게 해제할 수 있고, 코드를 간결하게 유지할 수 있습니다.
- AuthCloseable 인터페이스를 구현하면 try 구문이 종료될 때 객체의 close 메소드를 호출해서 자원을 닫아줍니다.
- InputStream 객체가 Closeable 을 구현하므로 FileInputStream 객체가 해제될 수 있습니다.
```java
public abstract class InputStream extends Object implements Closeable {
    ...
}

public interface Closeable extends AutoCloseable {
    void close() throws IOException;
}
```

```java
static void copy(String scr, String dst) throws IOException {
    InputStream in = new FileInputStream(src);
    try {
        OutputStream out = new FileInputStream(dst);
        
            byte[] buf = new byte[BUFFER_SIZE];
            int n;
            while((n = in.read(buf)) >= 0)
                out.write(buf, 0, n);
    }
}
```

### 3. 예외처리
- java7 에 추가된 Throwable 의 getSuppressed 메서드를 이용해 숨겨진 모든 예외를 가져올 수 있습니다.
```java
public static void main(String[] args) throws Exception {
    try {
        testException();
    } catch (Throwable e) {
        Throwable[] arr = e.getSuppressed();

        for (int i = 0; i < arr.length; i++) {
            System.out.println("Exceptions:" + arr[i]));
        }
    }
}
    
public static void testException() throws Exception {
    Exception suppressed = new ArrayIndexOutOfBoundsException();
    
    final IOException ioe = new IOException();
    ioe.addSuppressed(suppressed);

    throw ioe;
}
```


- catch 절과 함께 쓰면 다수의 예외처리를 명확하게 할 수 있습니다.
```java
static String firstLineOfFile(String path, String defaultVal) {
    // 예외발생 시 기본값을 반환하는 예시
    try(BufferedReader br = BufferedReader(new FileReader(path))) {
        return br.readLine();
    } catch(IOException e) {
        return defaultVal;
    }
}
```


### 4. 요약
- 해제해야하는 자원을 다룰때는 try-with-resources 를 사용합시다.
- 코드가 짧고 분명해집니다.
- catch 를 더해 예외정보를 명확하게 할 수 있습니다.