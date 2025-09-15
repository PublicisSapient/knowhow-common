/*
 *   Copyright 2014 CapitalOne, LLC.
 *   Further development Copyright 2022 Sapient Corporation.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.publicissapient.kpidashboard.common.repository.scm;

import com.publicissapient.kpidashboard.common.model.scm.User;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity operations.
 * 
 * Provides data access methods for user-related operations including
 * queries for finding users by various criteria and statistics.
 */
@Repository
public interface ScmUserRepository extends MongoRepository<User, String>, ScmUserRepositoryCustom {

    /**
     * Finds a user by username.
     * 
     * @param username the username to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by email address.
     * 
     * @param email the email address to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds a user by repository name and username (new unique constraint).
     *
     * @param repositoryName the repository name
     * @param username the username to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByRepositoryNameAndUsername(String repositoryName, String username);

    Optional<User> findByProcessorItemIdAndUsername(ObjectId processorItemId, String username);

    Optional<User> findByProcessorItemIdAndEmail(ObjectId processorItemId, String email);

    /**
     * Finds a user by repository name and email.
     *
     * @param repositoryName the repository name
     * @param email the email address to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByRepositoryNameAndEmail(String repositoryName, String email);

    void deleteByProcessorItemIdIn(List<ObjectId> processorItemIds);

    /**
     * Finds users by repository name.
     *
     * @param repositoryName the repository name
     * @param pageable pagination information
     * @return page of users for the specified repository
     */
    Page<User> findByRepositoryName(String repositoryName, Pageable pageable);

    /**
     * Finds a user by external platform ID.
     *
     * @param externalId the external ID to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByExternalId(String externalId);

    /**
     * Finds a user by repository name and external platform ID.
     *
     * @param repositoryName the repository name
     * @param externalId the external ID to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByRepositoryNameAndExternalId(String repositoryName, String externalId);

    /**
     * Finds users by username or email (case-insensitive).
     *
     * @param username the username to search for
     * @param email the email to search for
     * @return Optional containing the user if found
     */
    @Query("{'$or': [{'username': {'$regex': ?0, '$options': 'i'}}, {'email': {'$regex': ?1, '$options': 'i'}}]}")
    Optional<User> findByUsernameOrEmailIgnoreCase(String username, String email);

    /**
     * Finds all active users.
     * 
     * @param pageable pagination information
     * @return page of active users
     */
    Page<User> findByActiveTrue(Pageable pageable);

    /**
     * Finds all bot accounts.
     * 
     * @param pageable pagination information
     * @return page of bot users
     */
    Page<User> findByBotTrue(Pageable pageable);

    /**
     * Finds users who were last seen after a specific date.
     * 
     * @param date the date threshold
     * @param pageable pagination information
     * @return page of recently active users
     */
    Page<User> findByLastSeenAtAfter(LocalDateTime date, Pageable pageable);

    /**
     * Finds users by company name (case-insensitive).
     * 
     * @param company the company name to search for
     * @param pageable pagination information
     * @return page of users from the specified company
     */
    @Query("{'company': {'$regex': ?0, '$options': 'i'}}")
    Page<User> findByCompanyIgnoreCase(String company, Pageable pageable);

    /**
     * Finds users by location (case-insensitive).
     * 
     * @param location the location to search for
     * @param pageable pagination information
     * @return page of users from the specified location
     */
    @Query("{'location': {'$regex': ?0, '$options': 'i'}}")
    Page<User> findByLocationIgnoreCase(String location, Pageable pageable);

    /**
     * Searches users by display name or username containing the search term.
     * 
     * @param searchTerm the term to search for
     * @param pageable pagination information
     * @return page of matching users
     */
    @Query("{'$or': [{'displayName': {'$regex': ?0, '$options': 'i'}}, {'username': {'$regex': ?0, '$options': 'i'}}]}")
    Page<User> searchByDisplayNameOrUsername(String searchTerm, Pageable pageable);

    /**
     * Counts the number of active users.
     * 
     * @return count of active users
     */
    long countByActiveTrue();

    /**
     * Counts the number of bot accounts.
     * 
     * @return count of bot users
     */
    long countByBotTrue();

    /**
     * Finds users created within a date range.
     * 
     * @param startDate start of the date range
     * @param endDate end of the date range
     * @param pageable pagination information
     * @return page of users created within the date range
     */
    Page<User> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Finds users with incomplete profiles (missing username or email).
     * 
     * @param pageable pagination information
     * @return page of users with incomplete profiles
     */
    @Query("{'$or': [{'username': {'$exists': false}}, {'username': ''}, {'email': {'$exists': false}}, {'email': ''}]}")
    Page<User> findUsersWithIncompleteProfiles(Pageable pageable);

    /**
     * Finds the most recently created users.
     * 
     * @param limit maximum number of users to return
     * @return list of recently created users
     */
    @Query(value = "{}", sort = "{'createdAt': -1}")
    List<User> findRecentlyCreatedUsers(int limit);

    /**
     * Finds users who have been inactive for a specified number of days.
     * 
     * @param cutoffDate the cutoff date for inactivity
     * @param pageable pagination information
     * @return page of inactive users
     */
    @Query("{'$or': [{'lastSeenAt': {'$lt': ?0}}, {'lastSeenAt': {'$exists': false}}]}")
    Page<User> findInactiveUsers(LocalDateTime cutoffDate, Pageable pageable);

    /**
     * Checks if a user exists by username or email.
     * 
     * @param username the username to check
     * @param email the email to check
     * @return true if a user exists with the given username or email
     */
    @Query("{'$or': [{'username': ?0}, {'email': ?1}]}")
    boolean existsByUsernameOrEmail(String username, String email);
}