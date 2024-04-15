import { FormEvent, useRef, useState } from "react";
import { Button, Form } from "react-bootstrap";
import ReCAPTCHA from "react-google-recaptcha";
import { Link, useNavigate } from "react-router-dom";

import { userService } from "@/services/userService";
import { AuthenticatedUser, UserLoginRequest } from "@/types/user";

import styles from "./Auth.module.css";
import { useAppContext } from "@/contexts/AppContext";

export type AuthFormProps = {
  readonly type: "registration" | "login";
};

export function AuthForm({ type }: AuthFormProps) {
  const navigate = useNavigate();
  const { setUser } = useAppContext();

  const emailRef = useRef<HTMLInputElement>(null);
  const passwordRef = useRef<HTMLInputElement>(null);
  const captchaRef = useRef<ReCAPTCHA>(null);

  const [isEmailValid, setIsEmailValid] = useState<boolean>(false);
  const [isPasswordValid, setIsPasswordValid] = useState<boolean>(false);
  const [isCaptchaValid, setIsCaptchaValid] = useState<boolean>(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  function isValidEmail(email: string) {
    const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    return emailPattern.test(email);
  }

  function handleEmailChange() {
    setIsEmailValid(() => isValidEmail(emailRef.current?.value ?? ""));
  }

  function handlePasswordChange() {
    setIsPasswordValid(() => (passwordRef.current?.value.length ?? 0) >= 8);
  }

  function handleCaptchaChange(value: string | null) {
    setIsCaptchaValid(() => value !== null);
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const user: UserLoginRequest = {
      email: emailRef.current!.value,
      password: passwordRef.current!.value
    };

    const captcha = import.meta.env.PROD ? captchaRef.current!.getValue()! : "";

    try {
      const userId =
        type === "login"
          ? await userService.login(user, captcha)
          : await userService.postUser(user, captcha);

      const authenticatedUser: AuthenticatedUser = {
        id: userId
      };

      setUser(() => authenticatedUser);

      navigate("/");
    } catch (error) {
      const e = error as Error;
      setErrorMessage(() => e.message);
    }
  }

  return (
    <div className={`d-flex align-items-center justify-content-center ${styles.auth}`}>
      <Form className={styles.form} onSubmit={e => handleSubmit(e)}>
        {errorMessage && (
          <div className="alert alert-danger mt-3" role="alert">
            {errorMessage}
          </div>
        )}
        <Form.Group controlId="formBasicEmail">
          <Form.Label>Email address</Form.Label>
          <Form.Control
            type="email"
            placeholder="Enter email"
            onChange={() => handleEmailChange()}
            ref={emailRef}
            defaultValue={import.meta.env.DEV ? "sads1@fsdf.com" : ""}
            required
          />
        </Form.Group>

        <Form.Group controlId="formBasicPassword" className="mt-4">
          <Form.Label>Password</Form.Label>
          <Form.Control
            type="password"
            placeholder="Password"
            onChange={() => handlePasswordChange()}
            ref={passwordRef}
            defaultValue={import.meta.env.DEV ? "password123" : ""}
            required
          />
        </Form.Group>

        {import.meta.env.PROD && (
          <div className="mt-3 d-flex align-items-center justify-content-center ">
            <ReCAPTCHA
              sitekey={import.meta.env.VITE_CAPTCHA_SITE_KEY}
              ref={captchaRef}
              onChange={e => handleCaptchaChange(e)}
            />
          </div>
        )}

        <Button
          className="mt-4 w-100"
          variant="primary"
          type="submit"
          disabled={
            import.meta.env.PROD ? !isEmailValid || !isPasswordValid || !isCaptchaValid : false
          }
        >
          Login
        </Button>

        <div className="text-center mt-3">
          <Link
            className="text-body-secondary"
            to={type === "registration" ? "/login" : "/registration"}
          >
            {type === "registration" ? "Or login" : "Create an account"}
          </Link>
        </div>
      </Form>
    </div>
  );
}
