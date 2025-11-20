export class Login {
  static readonly type = '[Auth] Login';
  constructor(public payload: { email: string; senha: string }) {}
}

export class Logout {
  static readonly type = '[Auth] Logout';
}