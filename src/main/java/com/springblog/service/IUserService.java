package com.springblog.service;

import com.github.pagehelper.PageInfo;
import com.springblog.domain.User;
import com.springblog.dto.RegisterForm;

/**
 * User accounts: self-registration plus the lookups the admin screens need.
 */
public interface IUserService {

    User findByUsername(String username);

    /**
     * @throws com.springblog.exception.ResourceNotFoundException no such account
     */
    User findById(Integer id);

    /**
     * Create a ROLE_USER account from validated registration input.
     *
     * @throws com.springblog.exception.ConflictException username or email already taken
     */
    void register(RegisterForm form);

    boolean isUsernameExists(String username);

    boolean isEmailExists(String email);

    PageInfo<User> findPage(int page, int size);

    /**
     * Update an existing account. A blank password keeps the stored hash instead of hashing an
     * empty string, which would lock the account out.
     */
    void updateUser(User user);

    void deleteUser(Integer id);

    /** Enable or disable an account; disabled accounts fail authentication. */
    void toggleUserStatus(Integer id);
}
